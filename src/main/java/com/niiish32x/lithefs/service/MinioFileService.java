package com.niiish32x.lithefs.service;

import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import io.minio.errors.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MinioFileService {
    @SneakyThrows
    void uploadFile(MinioUploadReqDTO requestParam);

    @SneakyThrows
    void downloadFile(MinioDownloadReqDTO minioDownloadReqDTO);


    @SneakyThrows
    void downloadAllFile(MinioDownloadAllReqDTO requestParam);


    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    void downloadFileOverwrite(MinioDownloadReqDTO requestParam);

    @SneakyThrows
    void downloadAllFileOverwrite(MinioDownloadAllReqDTO requestParam);


    @SneakyThrows
    void shardingDownloadFile(MinioDownloadReqDTO requestParam);
}
