package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.service.SysDownloaderService;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class SysDownloaderServiceImpl implements SysDownloaderService {
    private final MinioInit minioInit;

    @Override
    public void downloadFile(String bucketName, String objectName, String downloadPath) {
        MinioClient minioClient = minioInit.init();
        try {

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(downloadPath + "/" + objectName)
                            .build()
            );

            System.out.println("文件下载成功！");

        } catch (MinioException | IOException e) {
            System.out.println("文件下载失败：" + e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
