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
public class SharingFileManagementThread implements Runnable{
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

    @SneakyThrows
    @Override
    public void run() {
        // 分片数量
        long numChunks = (long) Math.ceil((double) objectSize / chunkSize);
        System.out.println(numChunks);
        // 进行分片下载
        for (int i = 0 ; i < numChunks ; i++){
            // 当前分片的范围
            long offset = i * chunkSize;
            long length = Math.min(chunkSize, objectSize -  offset);

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
        }
        latch.countDown();
    }
}
