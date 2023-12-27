package com.niiish32x.lithefs.threads;


import io.minio.MinioClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 将大文件进行分片 并将各个分片分派给个线程 进行多线程下载
 */

@Data
@Slf4j
@RequiredArgsConstructor
public class MinioSharingFileManagementThread implements Runnable{
    private StringRedisTemplate stringRedisTemplate;
    // 下载文件目标大小
    private long objectSize;
    // 分片大小
    private long chunkSize;
    // 要进行下载的目标桶值
    private String bucketName;
    // 要下载的目标名
    private String objectName;
    // 本地下载地址
    private String downloadPath;
    private CopyOnWriteArrayList<String> chunkFileList = new CopyOnWriteArrayList<>();
    private MinioClient minioClient;

    public MinioSharingFileManagementThread(MinioClient minioClient,StringRedisTemplate stringRedisTemplate,String bucketName,String objectName,String downloadPath){
        this.minioClient = minioClient;
        this.downloadPath = downloadPath;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @SneakyThrows
    @Override
    public void run() {

//        // 初始化 断点下载 信息
//        stringRedisTemplate.opsForHash().put(bucketName + "/" + objectName ,"init","init");

        // 分片数量
        long numChunks = (long) Math.ceil((double) objectSize / chunkSize);
        CountDownLatch countDownLatch = new CountDownLatch((int) numChunks);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


//         线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                16,
                32,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());

        Set<String>set = stringRedisTemplate.opsForSet().members(bucketName + "/" + objectName);

        // 先从Redis中去获取之前已经下载过的分片地址
        if (set != null){
            chunkFileList = new CopyOnWriteArrayList<>(set.stream().toList());
        }

//        chunkFileList.stream().forEach(e-> System.out.println(e));


        // 进行分片下载
        for (int i = 0 ; i < numChunks ; i++){
            // 当前分片的范围
            long offset = i * chunkSize;
            long length = Math.min(chunkSize, objectSize -  offset);

            MinioChunkFileDownloadThread minioChunkFileDownloadThread
                    = new MinioChunkFileDownloadThread(minioClient,stringRedisTemplate,bucketName,objectName,downloadPath,offset,length,chunkFileList,countDownLatch);
//
            threadPoolExecutor.execute(minioChunkFileDownloadThread);
//            minioChunkFileDownloadThread.run();
        }

        countDownLatch.await();


        // 用于线程计数
        CountDownLatch latch = new CountDownLatch(1);

        stopWatch.stop();
        log.info("完成所有分片的下载");
        System.out.println("完成所有分片的下载 + 耗时:" + stopWatch.getTotalTimeSeconds()+ "秒");
//        stopWatch.start();
        // 分片文件合并线程 对分片进行合并
        MinioShardingFileMergeThread minioShardingFileMergeThread = new MinioShardingFileMergeThread(latch);
        minioShardingFileMergeThread.setChunkFileList(chunkFileList);
        minioShardingFileMergeThread.setMergeFile(downloadPath + "/" + objectName);
        minioShardingFileMergeThread.run();

        // 只有等到所有分片文件合并完 再进行分片进行删除
        latch.await();



//        stopWatch.stop();
//        System.out.println("完成所有分片的合并 + 耗时:" + stopWatch.getTotalTimeSeconds() + "秒");

        log.info("开始删除已下载的分片");
        // 分片文件删除线程 删除分片文件
        MinioShardingChunkFileDeleteThread minioShardingChunkFileDeleteThread = new MinioShardingChunkFileDeleteThread();
        minioShardingChunkFileDeleteThread.setChunkFileList(chunkFileList);
        threadPoolExecutor.execute(minioShardingChunkFileDeleteThread);
        threadPoolExecutor.shutdown();

        // 删除Redis中hash 因为不再需要断点续传
        stringRedisTemplate.delete(bucketName + "/" + objectName);

//        minioShardingChunkFileDeleteThread.run();
    }
}

