package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.service.MinioFileService;
import com.niiish32x.lithefs.threads.MinioSharingFileManagementThread;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements MinioFileService {

    private final MinioInit minioInit;
    @SneakyThrows
    @Override
    public void uploadFile(MinioUploadReqDTO requestParam) {
        MinioClient minioClient = minioInit.init();
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(requestParam.getBucketName())
                        .object(requestParam.getObjectName())
                        .filename(requestParam.getUploadFileName())
                        .build()
            );
    }


    @SneakyThrows
    @Override
    public void multiPartUploadFile(MinioUploadReqDTO requestParam) {
        MinioClient minioClient = minioInit.init();
        System.out.println(requestParam.getUploadFileName());
        System.out.println(requestParam.getObjectName());
        File file = new File(requestParam.getUploadFileName());

        InputStream inputStream = new FileInputStream(file);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(requestParam.getBucketName())
                        .object(requestParam.getObjectName())
                        .stream(inputStream,file.length(),5 * 1024  * 1024)
                        .build()
        );

        inputStream.close();
    }


    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    @Override
    public void downloadFile(MinioDownloadReqDTO minioDownloadReqDTO) {
        MinioClient minioClient = minioInit.init();
        log.info("开始单文件下载");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(minioDownloadReqDTO.getBucketName())
                        .object(minioDownloadReqDTO.getObjectName())
                        .filename(minioDownloadReqDTO.getDownloadPath() + "/" + minioDownloadReqDTO.getObjectName())
                        .build()
            );

        stopWatch.stop();
        System.out.println("完成单文件下载， 耗时:" + stopWatch.getTotalTimeSeconds() + "秒");
    }


    @Override
    @SneakyThrows
    public void downloadAllFile(MinioDownloadAllReqDTO requestParam){
        MinioClient minioClient = minioInit.init();

        String bucketName = requestParam.getBucketName();
        String downloadPath = requestParam.getDownloadPath();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> object: objects){
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object.get().objectName())
                            .filename(downloadPath + "/"  + object.get().objectName())
                            .build()
            );
        }
    }


    @SneakyThrows
    @Override
    public void downloadFileOverwrite(MinioDownloadReqDTO requestParam){
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();
        Path targetFile = Paths.get(downloadPath + "/" + objectName);
        if (Files.exists(targetFile)){
            // 删除原来文件
            Files.delete(targetFile);
        }

        // 然后再进行下载
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(downloadPath + "/" + objectName)
                        .build()
        );
    }


    @Override
    @SneakyThrows
    public void downloadAllFileOverwrite(MinioDownloadAllReqDTO requestParam){

        String bucketName = requestParam.getBucketName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> object: objects){
            String objectName = object.get().objectName();
            Path targetFile = Paths.get(downloadPath + "/" + objectName);

            if (Files.exists(targetFile)){
                // 删除原来文件
                Files.delete(targetFile);
            }

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object.get().objectName())
                            .filename(downloadPath + "/"  + object.get().objectName())
                            .build()
            );
        }
    }

    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    @Override
    public void shardingDownloadFile(MinioDownloadReqDTO requestParam){
//        log.info("开始分片文件下载");
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();

        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();
        StatObjectResponse statedObject = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

        // 目标大小
        long objectSize = statedObject.size();
        // 分片大小
        long chunkSize = 80 * 1024 * 1024; // 4MB

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        500,
        800,
        10,
        TimeUnit.DAYS,
        new ArrayBlockingQueue<>(10),
        new ThreadPoolExecutor.CallerRunsPolicy());


        // 线程分片下载线程
        MinioSharingFileManagementThread minioSharingFileManagementThread
                = new MinioSharingFileManagementThread(minioClient,bucketName,objectName,downloadPath);
        minioSharingFileManagementThread.setChunkSize(chunkSize);
        minioSharingFileManagementThread.setObjectSize(objectSize);
        threadPoolExecutor.execute(minioSharingFileManagementThread);
//        minioSharingFileManagementThread.run();

//        stopWatch.stop();
//        log.info("完成分片文件下载, 文件大小为: " );
//        System.out.println("下载大小为: " + objectSize / 1024 * 1024 + "MB " +   "总耗时为: " + stopWatch.getTotalTimeSeconds());
    }
}











































