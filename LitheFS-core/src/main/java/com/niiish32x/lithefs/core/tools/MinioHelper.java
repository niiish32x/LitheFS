package com.niiish32x.lithefs.core.tools;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 对于Minio文件 传输进行辅助的工具类
 */
@Slf4j
public class MinioHelper {

    /**
     * 判断要下载的路径的文件夹是否存在 如果不存在则创建相应的文件夹
     * @param downloadPath
     */
    public static void downloadFilePathHelper(String downloadPath){
        File folder = new File(downloadPath);

        if (!folder.exists()){
            if (folder.mkdirs()){
                log.info("新建文件夹:" + folder.getName() + "用于文件下载");
            }else {
                log.error("新建文件夹:" + folder.getName() + "失败");
            }
        }
    }

    /**
     * 判断要上传bucket是否存在 如果不存在则直接创建
     */
    @SneakyThrows
    public static void bucketHelper(MinioClient minioClient, String bucketName){
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!bucketExists){
            log.warn("bucket: " + bucketName + "不存在，创建相应的bucket");

            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}
