package com.niiish32x.lithefs.threads;


import io.minio.MinioClient;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.StopWatch;


import java.util.concurrent.*;

/**
 * 将大文件进行分片 并将各个分片分派给个线程 进行多线程下载
 */

@Data
public class MinioSharingFileManagementThread implements Runnable{
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

    public MinioSharingFileManagementThread(MinioClient minioClient,String bucketName,String objectName,String downloadPath){
        this.minioClient = minioClient;
        this.downloadPath = downloadPath;
        this.bucketName = bucketName;
        this.objectName = objectName;
    }

    @SneakyThrows
    @Override
    public void run() {
        // 分片数量
        long numChunks = (long) Math.ceil((double) objectSize / chunkSize);
        CountDownLatch countDownLatch = new CountDownLatch((int) numChunks);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


//         线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                500,
                800,
                10,
                TimeUnit.DAYS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());


        // 进行分片下载
        for (int i = 0 ; i < numChunks ; i++){
            // 当前分片的范围
            long offset = i * chunkSize;
            long length = Math.min(chunkSize, objectSize -  offset);

            MinioChunkFileDownloadThread minioChunkFileDownloadThread
                    = new MinioChunkFileDownloadThread(minioClient,bucketName,objectName,downloadPath,offset,length,chunkFileList,countDownLatch);
//
            threadPoolExecutor.execute(minioChunkFileDownloadThread);
//            minioChunkFileDownloadThread.run();
        }

        countDownLatch.await();


        // 用于线程计数
        CountDownLatch latch = new CountDownLatch(1);

        stopWatch.stop();
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


        // 分片文件删除线程 删除分片文件
        MinioShardingChunkFileDeleteThread minioShardingChunkFileDeleteThread = new MinioShardingChunkFileDeleteThread();
        minioShardingChunkFileDeleteThread.setChunkFileList(chunkFileList);
        minioShardingChunkFileDeleteThread.run();

    }
}
