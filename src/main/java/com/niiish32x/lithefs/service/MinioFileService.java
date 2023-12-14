package com.niiish32x.lithefs.service;

import io.minio.errors.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MinioFileService {
    void uploadFile(String bucketName, String objectName, String uploadFileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void downloadFile(String bucketName, String objectName, String downloadPath);


    @SneakyThrows
    void downloadAllFile(String bucketName, String downloadPath);

    @SneakyThrows
    void downloadFileOverwrite(String bucketName, String objectName, String downloadPath);

    @SneakyThrows
    void downloadAllFileOverwrite(String bucketName, String downloadPath);
}
