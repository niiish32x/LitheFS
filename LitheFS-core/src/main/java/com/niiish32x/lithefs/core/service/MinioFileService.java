package com.niiish32x.lithefs.core.service;

import com.niiish32x.lithefs.core.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.core.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.core.dto.req.MinioRemoveFileDTO;
import com.niiish32x.lithefs.core.dto.req.MinioUploadReqDTO;
import lombok.SneakyThrows;


public interface MinioFileService {
    @SneakyThrows
    void removeFile(MinioRemoveFileDTO requestParam);

    @SneakyThrows
    void uploadFile(MinioUploadReqDTO requestParam);

    @SneakyThrows
    void multiPartUploadFile(MinioUploadReqDTO requestParam);

    @SneakyThrows
    void uploadFilePlus(MinioUploadReqDTO requestParam);

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
