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
import java.util.concurrent.CountDownLatch;

/**
 * 将大文件进行分片 并将各个分片分派给个线程 进行多线程下载
 */

@Data
public class MinioSharingFileManagementThread implements Runnable{
    private final CountDownLatch latch;
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

    public MinioSharingFileManagementThread(MinioClient minioClient,String bucketName,String objectName,String downloadPath,CountDownLatch latch){
        this.minioClient = minioClient;
        this.downloadPath = downloadPath;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.latch = latch;
    }

    @SneakyThrows
    @Override
    public void run() {
        // 分片数量
        long numChunks = (long) Math.ceil((double) objectSize / chunkSize);
        CountDownLatch countDownLatch = new CountDownLatch((int) numChunks);
        // 进行分片下载
        for (int i = 0 ; i < numChunks ; i++){
            // 当前分片的范围
            long offset = i * chunkSize;
            long length = Math.min(chunkSize, objectSize -  offset);

            MinioChunkFileDownloadThread minioChunkFileDownloadThread
                    = new MinioChunkFileDownloadThread(minioClient,bucketName,objectName,downloadPath,offset,length,chunkFileList,countDownLatch);
            minioChunkFileDownloadThread.run();
        }

        countDownLatch.await();
        latch.countDown();
    }
}
