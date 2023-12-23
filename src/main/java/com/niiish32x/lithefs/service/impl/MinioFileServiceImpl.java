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
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

//    @SneakyThrows
//    public void shardingUploadFile(String bucketName, String objectName, String uploadFileName){
//
//    }uploadFileName

    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    @Override
    public void downloadFile(MinioDownloadReqDTO minioDownloadReqDTO) {
        MinioClient minioClient = minioInit.init();
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(minioDownloadReqDTO.getBucketName())
                        .object(minioDownloadReqDTO.getObjectName())
                        .filename(minioDownloadReqDTO.getDownloadPath() + "/" + minioDownloadReqDTO.getObjectName())
                        .build()
            );
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
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getBucketName();
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
        long chunkSize = 4 * 1024 * 1024; // 4MB


        // 线程分片下载线程
        MinioSharingFileManagementThread minioSharingFileManagementThread
                = new MinioSharingFileManagementThread(minioClient,bucketName,objectName,downloadPath);
        minioSharingFileManagementThread.setChunkSize(chunkSize);
        minioSharingFileManagementThread.setObjectSize(objectSize);
        minioSharingFileManagementThread.run();

    }
}











































