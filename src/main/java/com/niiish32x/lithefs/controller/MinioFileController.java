package com.niiish32x.lithefs.controller;

import com.niiish32x.lithefs.service.MinioFileService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class MinioFileController {
    private MinioFileService minioFileService;

    /**
     *
     * @param bucketName 要进行下载的桶
     * @param objectName 要下载的文件名
     * @param downloadPath 本地存储的文件路径
     */
    @PostMapping("/api/minio/download")
    public void MinioDownload(@RequestParam("bucketName")String bucketName, @RequestParam("objectName") String objectName, @RequestParam("downloadPath") String downloadPath){
        minioFileService.downloadFile(bucketName,objectName,downloadPath);
    }



    /**
     *
     * @param bucketName  要存储的桶
     * @param objectName  要存储的对象
     * @param uploadFileName    要存储的文件具体路径
     * @return
     */
    @PostMapping("/api/minio/upload")
    public void MinioUpload(@RequestParam("bucketName") String bucketName,@RequestParam("objectName") String objectName, @RequestParam("uploadFileName") String uploadFileName ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioFileService.uploadFile(bucketName,objectName,uploadFileName);
    }

}
