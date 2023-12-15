package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.service.MinioFileService;
import com.niiish32x.lithefs.threads.ShardingChunkFileDeleteThread;
import com.niiish32x.lithefs.threads.ShardingFileMergeThread;
import com.niiish32x.lithefs.threads.SharingFileManagementThread;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements MinioFileService {

    private final MinioInit minioInit;

    @SneakyThrows
    @Override
    public void uploadFile(String bucketName, String objectName, String uploadFileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = minioInit.init();
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(uploadFileName)
                        .build()
            );
    }

    @SneakyThrows
    @Override
    public void downloadFile(String bucketName, String objectName, String downloadPath) {
        MinioClient minioClient = minioInit.init();
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
    public void downloadAllFile(String bucketName, String downloadPath){
        MinioClient minioClient = minioInit.init();

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
    public void downloadFileOverwrite(String bucketName, String objectName, String downloadPath){
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
    public void downloadAllFileOverwrite(String bucketName, String downloadPath){
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

    @SneakyThrows
    @Override
    public void shardingDownloadFile(String bucketName, String objectName, String downloadPath){
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

        CountDownLatch latch1 = new CountDownLatch(1);
        SharingFileManagementThread sharingFileManagementThread = new SharingFileManagementThread(latch1);
        sharingFileManagementThread.setBucketName(bucketName);
        sharingFileManagementThread.setDownloadPath(downloadPath);
        sharingFileManagementThread.setMinioClient(minioClient);
        sharingFileManagementThread.setChunkSize(chunkSize);
        sharingFileManagementThread.setObjectName(objectName);
        sharingFileManagementThread.setObjectSize(objectSize);
        sharingFileManagementThread.run();
        ArrayList<String> chunkFileList = sharingFileManagementThread.getChunkFileList();
        latch1.await();

        // 用于线程计数
        CountDownLatch latch2 = new CountDownLatch(1);

        // 分片文件合并线程 对分片进行合并
        ShardingFileMergeThread shardingFileMergeThread = new ShardingFileMergeThread(latch2);
        shardingFileMergeThread.setChunkFileList(chunkFileList);
        shardingFileMergeThread.setMergeFile(downloadPath + "/" + objectName);
        shardingFileMergeThread.run();

        // 只有等到所有分片文件合并完 再进行分片进行删除
        latch2.await();

        // 分片文件删除线程 删除分片文件
        ShardingChunkFileDeleteThread shardingChunkFileDeleteThread = new ShardingChunkFileDeleteThread();
        shardingChunkFileDeleteThread.setChunkFileList(chunkFileList);
        shardingChunkFileDeleteThread.run();
    }
}











































