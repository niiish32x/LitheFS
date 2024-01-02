package com.niiish32x.lithefs.core.threads;


import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 根据MinioShardingFileMergeThread 的分片结果对 文件进行分片下载
 */

@Data
@Slf4j
public class MinioChunkFileDownloadThread implements Runnable{
    private CountDownLatch countDownLatch;
    private final StringRedisTemplate stringRedisTemplate;
    private long offset;
    private long length;
    private MinioClient minioClient;
    private String bucketName;
    private String objectName;
    private String downloadPath;
    private CopyOnWriteArrayList<String> chunkFileList;


    public MinioChunkFileDownloadThread(MinioClient minioClient, StringRedisTemplate stringRedisTemplate,  String bucketName, String objectName, String downloadPath, long offset, long length ,CopyOnWriteArrayList<String>  chunkFileList, CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.downloadPath = downloadPath;
        this.offset = offset;
        this.length = length;
        this.chunkFileList = chunkFileList;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @SneakyThrows
    @Override
    public void run() {
        String localFilePath = downloadPath + "/" + offset + objectName ;
        // 在Redis中 已有该分片的下载信息
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(bucketName + "/" + objectName, localFilePath))){
            log.info("分片: " + offset + " 已完成下载");
            countDownLatch.countDown();
            return;
        }

        InputStream chunkObject = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build()
        );





        chunkFileList.add(localFilePath);
        Path localFile = Path.of(localFilePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(chunkObject);
        Files.copy(bufferedInputStream,localFile, StandardCopyOption.REPLACE_EXISTING);

        // 完成分片下载后 将该分片的信息 存入hash
        stringRedisTemplate.opsForSet().add(bucketName + "/" + objectName,localFilePath);


        countDownLatch.countDown();
    }
}
