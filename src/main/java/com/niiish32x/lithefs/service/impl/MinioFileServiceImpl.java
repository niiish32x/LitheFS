package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.service.MinioFileService;
import com.niiish32x.lithefs.threads.ShardingFileMergeThread;
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
import java.nio.file.StandardCopyOption;
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
        System.out.println("xxx");
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
        // 分片数量
        long numChunks = (long) Math.ceil((double) objectSize / chunkSize);


        ArrayList<String> chunkFileList = new ArrayList<>();

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

        // 用于线程计数
        CountDownLatch latch = new CountDownLatch(1);

        // 使用分片文件合并线程 对分片进行合并
        ShardingFileMergeThread shardingFileMergeThread = new ShardingFileMergeThread(latch);
        shardingFileMergeThread.setChunkFileList(chunkFileList);
        shardingFileMergeThread.setMergeFile(downloadPath + "/" + objectName);
        shardingFileMergeThread.run();

        // 只有等到所有分片文件合并完 再进行分片进行删除
        latch.await();
        System.out.println(chunkFileList);
        // 删除之前的分片 文件
        for (String chunkFile : chunkFileList){
            File file = new File(chunkFile);
            if (file.exists()){
                if (file.delete()){
                    System.out.println("删除分片" + chunkFile);
                }else {
                    System.out.println("分片"+chunkFile + "删除失败");
                }
            }else {
                System.out.println("分片" + chunkFile + "不存在");
            }
        }
    }
}











































