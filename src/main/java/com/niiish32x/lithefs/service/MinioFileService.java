package com.niiish32x.lithefs.service;

import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioRemoveFileDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import lombok.SneakyThrows;

public interface MinioFileService {
    @SneakyThrows
    void removeFile(MinioRemoveFileDTO requestParam);

    @SneakyThrows
    void uploadFile(MinioUploadReqDTO requestParam);

    @SneakyThrows
    void multiPartUploadFile(MinioUploadReqDTO requestParam);

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
