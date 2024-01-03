package com.niiish32x.lithefs.core.controller;

import com.niiish32x.lithefs.core.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.core.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.core.dto.req.MinioRemoveFileDTO;
import com.niiish32x.lithefs.core.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.core.service.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/minio")
public class MinioFileController {
    private final MinioFileService minioFileService;
    @PostMapping("/download")
    public void MinioDownload(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.downloadFile(requestParam);
    }

    @PostMapping("/downloadAll")
    public void MinioDownloadAll(@RequestBody MinioDownloadAllReqDTO requestParam){
        minioFileService.downloadAllFile(requestParam);
    }

    @PostMapping("/downloadOverwrite")
    public void MinioDownloadOverwrite(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.downloadFileOverwrite(requestParam);
    }

    @PostMapping("/downloadAllOverwrite")
    public void MinioDownloadAllOverwrite(@RequestBody MinioDownloadAllReqDTO requestParam){
        minioFileService.downloadAllFileOverwrite(requestParam);
    }

    @PostMapping("/shardingDownload")
    public void MinioShardingDownload(@RequestBody MinioDownloadReqDTO requestParam){
        minioFileService.shardingDownloadFile(requestParam);
    }

    @PostMapping("/upload")
    public void MinioUpload(@RequestBody MinioUploadReqDTO requestParam) {
        minioFileService.uploadFilePlus(requestParam);
    }


//    @PostMapping("/api/minio/multiPartUpload")
//    public void MinioMultiPartUpload(@RequestBody MinioUploadReqDTO requestParam) {
//        minioFileService.multiPartUploadFile(requestParam);
//    }

    @DeleteMapping("/remove")
    public void MinioRemoveFile(@RequestBody MinioRemoveFileDTO requestParam){
        minioFileService.removeFile(requestParam);
    }
}
