package com.niiish32x.lithefs.threads;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ch.qos.logback.core.CoreConstants.CORE_POOL_SIZE;
import static ch.qos.logback.core.CoreConstants.MAX_POOL_SIZE;

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
    private ArrayList<String> chunkFileList = new ArrayList<>();
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


        // 线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());


        // 进行分片下载
        for (int i = 0 ; i < numChunks ; i++){
            // 当前分片的范围
            long offset = i * chunkSize;
            long length = Math.min(chunkSize, objectSize -  offset);

            MinioChunkFileDownloadThread minioChunkFileDownloadThread
                    = new MinioChunkFileDownloadThread(minioClient,bucketName,objectName,downloadPath,offset,length,chunkFileList,countDownLatch);

            threadPoolExecutor.execute(minioChunkFileDownloadThread);
//            minioChunkFileDownloadThread.run();
        }

        countDownLatch.await();

        // 用于线程计数
        CountDownLatch latch = new CountDownLatch(1);

        // 分片文件合并线程 对分片进行合并
        MinioShardingFileMergeThread minioShardingFileMergeThread = new MinioShardingFileMergeThread(latch);
        minioShardingFileMergeThread.setChunkFileList(chunkFileList);
        minioShardingFileMergeThread.setMergeFile(downloadPath + "/" + objectName);
        minioShardingFileMergeThread.run();

        // 只有等到所有分片文件合并完 再进行分片进行删除
        latch.await();

        // 分片文件删除线程 删除分片文件
        MinioShardingChunkFileDeleteThread minioShardingChunkFileDeleteThread = new MinioShardingChunkFileDeleteThread();
        minioShardingChunkFileDeleteThread.setChunkFileList(chunkFileList);
        minioShardingChunkFileDeleteThread.run();

    }
}
