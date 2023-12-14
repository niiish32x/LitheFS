package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.service.MinioFileService;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            System.out.println(object.get().objectName());
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object.get().objectName())
                            .filename(downloadPath + "/"  + object.get().objectName())
                            .build()
            );
        }
    }
}























