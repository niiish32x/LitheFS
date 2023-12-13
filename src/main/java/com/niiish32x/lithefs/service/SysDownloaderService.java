package com.niiish32x.lithefs.service;

public interface SysDownloaderService {

    void downloadFile(String bucketName, String objectName, String downloadPath);
}
