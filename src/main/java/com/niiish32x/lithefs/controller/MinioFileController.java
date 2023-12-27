package com.niiish32x.lithefs.controller;

import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.service.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MinioFileController {
    private final MinioFileService minioFileService;
    @PostMapping("/api/minio/download")
    public void MinioDownload(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.downloadFile(requestParam);
    }

    @PostMapping("/api/minio/downloadAll")
    public void MinioDownloadAll(@RequestBody MinioDownloadAllReqDTO requestParam){
        minioFileService.downloadAllFile(requestParam);
    }

    @PostMapping("/api/minio/downloadOverwrite")
    public void MinioDownloadOverwrite(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.downloadFileOverwrite(requestParam);
    }

    @PostMapping("/api/minio/downloadAllOverwrite")
    public void MinioDownloadAllOverwrite(@RequestBody MinioDownloadAllReqDTO requestParam){
        minioFileService.downloadAllFileOverwrite(requestParam);
    }

    @PostMapping("/api/minio/shardingDownload")
    public void MinioShardingDownload(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.shardingDownloadFile(requestParam);
    }

    @PostMapping("/api/minio/upload")
    public void MinioUpload(@RequestBody MinioUploadReqDTO requestParam) {
        minioFileService.uploadFile(requestParam);
    }


    @PostMapping("/api/minio/multiPartUpload")
    public void MinioMultiPartUpload(@RequestBody MinioUploadReqDTO requestParam) {
        minioFileService.multiPartUploadFile(requestParam);
    }


}
