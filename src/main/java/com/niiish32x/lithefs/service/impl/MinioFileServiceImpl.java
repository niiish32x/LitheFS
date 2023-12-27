package com.niiish32x.lithefs.service.impl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.niiish32x.lithefs.dto.req.MinioRemoveFileDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadAllReqDTO;
import com.niiish32x.lithefs.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.service.MinioFileService;
import com.niiish32x.lithefs.threads.MinioSharingFileManagementThread;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements MinioFileService {

    private final MinioInit minioInit;
//    private final RBloomFilter<String> rBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @SneakyThrows
    public void removeFile(MinioRemoveFileDTO requestParam){
        MinioClient minioClient = minioInit.init();
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();

        log.info("开始删除文件");

        // 删除Redis中的内容 否则 由于秒传判断后续就无法 再上传文件
        String key = (String) stringRedisTemplate.opsForHash().get("MinioUploadFileHash",objectName);
        stringRedisTemplate.opsForHash().delete("MinioUploadFileHash",key);

        // 删除文件
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );



        log.info("完成文件删除");
    }

    @SneakyThrows
    @Override
    public void uploadFile(MinioUploadReqDTO requestParam) {
        MinioClient minioClient = minioInit.init();
        String fileName = requestParam.getUploadFileName();
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();


        log.info("开始执行文件上传");

        // 文件秒传传 判断
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        File file = new File(requestParam.getUploadFileName());
        String digestHex = md5.digestHex(file);


        if (stringRedisTemplate.opsForHash().get("MinioUploadFileHash",digestHex) != null){
            log.warn("文件已经上传在路径: " + stringRedisTemplate.opsForHash().get("MinioUploadFileHash",digestHex));
//            System.out.println();
            return;
        }

        String uploadURL = bucketName+ "/" +objectName;
//        rBloomFilter.add(digestHex);
        // 存储 文件编码 对应 文件的放置的位置的URL 用于秒传操作时 直接输出文件具体位子
        stringRedisTemplate.opsForHash().put("MinioUploadFileHash",digestHex,uploadURL);
        // 存储 对象名 及其 对应编码 用于后续的删除操作
        stringRedisTemplate.opsForHash().put("MinioUploadFileHash",objectName,digestHex);

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build()
        );

        log.info("完成文件上传");
    }


    @SneakyThrows
    @Override
    public void multiPartUploadFile(MinioUploadReqDTO requestParam) {
        MinioClient minioClient = minioInit.init();
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();

        log.info("开始执行分片文件上传");

        File file = new File(requestParam.getUploadFileName());

        // 文件秒传传 判断
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String digestHex = md5.digestHex(file);
        if (stringRedisTemplate.opsForHash().get("MinioUploadFileHash",digestHex) != null){
            log.warn("文件已经上传在路径: " + stringRedisTemplate.opsForHash().get("MinioUploadFileHash",digestHex));
//            System.out.println();
            return;
        }
        String uploadURL = bucketName+ "/" +objectName;
//        rBloomFilter.add(digestHex);
        stringRedisTemplate.opsForHash().put("MinioUploadFileHash",digestHex,uploadURL);

        InputStream inputStream = new FileInputStream(file);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream,file.length(),5 * 1024  * 1024)
                        .build()
        );

        inputStream.close();
    }


    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    @Override
    public void downloadFile(MinioDownloadReqDTO minioDownloadReqDTO) {
        MinioClient minioClient = minioInit.init();
        log.info("开始单文件下载");
        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();

        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(minioDownloadReqDTO.getBucketName())
                        .object(minioDownloadReqDTO.getObjectName())
                        .filename(minioDownloadReqDTO.getDownloadPath() + "/" + minioDownloadReqDTO.getObjectName())
                        .build()
            );

//        stopWatch.stop();
        System.out.println("完成单文件下载， 耗时:" + stopWatch.getTotalTimeSeconds() + "秒");
    }


    @Override
    @SneakyThrows
    public void downloadAllFile(MinioDownloadAllReqDTO requestParam){
        MinioClient minioClient = minioInit.init();

        String bucketName = requestParam.getBucketName();
        String downloadPath = requestParam.getDownloadPath();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> object: objects){
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object.get().objectName())
                            .filename(downloadPath + "/"  + object.get().objectName())
                            .build()
            );
        }
    }


    @SneakyThrows
    @Override
    public void downloadFileOverwrite(MinioDownloadReqDTO requestParam){
        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();
        Path targetFile = Paths.get(downloadPath + "/" + objectName);
        if (Files.exists(targetFile)){
            // 删除原来文件
            Files.delete(targetFile);
        }

        // 然后再进行下载
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(downloadPath + "/" + objectName)
                        .build()
        );
    }


    @Override
    @SneakyThrows
    public void downloadAllFileOverwrite(MinioDownloadAllReqDTO requestParam){

        String bucketName = requestParam.getBucketName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();

        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> object: objects){
            String objectName = object.get().objectName();
            Path targetFile = Paths.get(downloadPath + "/" + objectName);

            if (Files.exists(targetFile)){
                // 删除原来文件
                Files.delete(targetFile);
            }

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object.get().objectName())
                            .filename(downloadPath + "/"  + object.get().objectName())
                            .build()
            );
        }
    }

    // String bucketName, String objectName, String downloadPath
    @SneakyThrows
    @Override
    public void shardingDownloadFile(MinioDownloadReqDTO requestParam){
        log.info("开始分片文件下载");
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();

        String bucketName = requestParam.getBucketName();
        String objectName = requestParam.getObjectName();
        String downloadPath = requestParam.getDownloadPath();

        MinioClient minioClient = minioInit.init();
        StatObjectResponse statedObject = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

        // 目标大小
        long objectSize = statedObject.size();
        // 分片大小
        long chunkSize = 6 * 1024 * 1024; // 4MB

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
//        16,
//        32,
//        10,
//        TimeUnit.SECONDS,
//        new ArrayBlockingQueue<>(10),
//        new ThreadPoolExecutor.CallerRunsPolicy());


        // 线程分片下载线程
        MinioSharingFileManagementThread minioSharingFileManagementThread
                = new MinioSharingFileManagementThread(minioClient,stringRedisTemplate,bucketName,objectName,downloadPath);
        minioSharingFileManagementThread.setChunkSize(chunkSize);
        minioSharingFileManagementThread.setObjectSize(objectSize);
        minioSharingFileManagementThread.run();
//        threadPoolExecutor.execute(minioSharingFileManagementThread);

        log.info("完成分片下载");

//        minioSharingFileManagementThread.run();

//        stopWatch.stop();
//        log.info("完成分片文件下载, 文件大小为: " );
//        System.out.println("下载大小为: " + objectSize / 1024 * 1024 + "MB " +   "总耗时为: " + stopWatch.getTotalTimeSeconds());
    }
}











































