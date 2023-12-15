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
 * 根据MinioShardingFileMergeThread 的分片结果对 文件进行分片下载
 */

@Data
public class MinioChunkFileDownloadThread implements Runnable{
    private CountDownLatch countDownLatch;
    private long offset;
    private long length;
    private MinioClient minioClient;
    private String bucketName;
    private String objectName;
    private String downloadPath;
    private ArrayList<String> chunkFileList;

    public MinioChunkFileDownloadThread(MinioClient minioClient, String bucketName, String objectName, String downloadPath, long offset, long length ,ArrayList<String> chunkFileList, CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.downloadPath = downloadPath;
        this.offset = offset;
        this.length = length;
        this.chunkFileList = chunkFileList;
    }

    @SneakyThrows
    @Override
    public void run() {
        InputStream chunkObject = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build()
        );

        String localFilePath = downloadPath + "/" + offset + objectName ;
        chunkFileList.add(localFilePath);
        Path localFile = Path.of(localFilePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(chunkObject);
        Files.copy(bufferedInputStream,localFile, StandardCopyOption.REPLACE_EXISTING);
        countDownLatch.countDown();
    }
}
