package com.niiish32x.lithefs.controller;

import com.niiish32x.lithefs.service.SysDownloaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DownloadController {
    private final SysDownloaderService sysDownloaderService;
    @PostMapping("/api/minio/download")
    public void MinioDownload(@RequestParam("bucketName")String bucketName,@RequestParam("objectName") String objectName,@RequestParam("downloadPath") String downloadPath){
        sysDownloaderService.downloadFile(bucketName,objectName,downloadPath);
    }
}
