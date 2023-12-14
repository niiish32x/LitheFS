package com.niiish32x.lithefs.controller;

import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.service.MinioFileService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class MinioFileController {
    private final MinioFileService minioFileService;

    @PostMapping("/api/minio/download")
    public void MinioDownload(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.downloadFile(requestParam.getBucketName(), requestParam.getObjectName(), requestParam.getDownloadPath());
    }

    @PostMapping("/api/minio/downloadAll")
    public void MinioDownload(@RequestBody MinioDownloadAllReqDTO requestParam){
        minioFileService.downloadAllFile(requestParam.getBucketName(), requestParam.getDownloadPath());
    }

    @PostMapping("/api/minio/upload")
    public void MinioUpload(@RequestBody MinioUploadReqDTO requestParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioFileService.uploadFile(requestParam.getBucketName(), requestParam.getObjectName(), requestParam.getUploadFileName());
    }
}
