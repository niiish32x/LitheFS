package com.niiish32x.lithefs.scheduler.service.impl;

import com.niiish32x.lithefs.scheduler.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.scheduler.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.scheduler.remote.MinioServiceClient;
import com.niiish32x.lithefs.scheduler.service.MinioTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MinioTaskServiceImpl implements MinioTaskService {
    private final MinioServiceClient minioServiceClient;

    @Override
    public void startDownloadFileTask(MinioDownloadReqDTO requestParam){
        log.info("开启下载任务");
        log.info("开始调用服务");
        minioServiceClient.download(requestParam);
        log.info("服务调用完毕");
    }

    @Override
    public void startUploadFileTask(MinioUploadReqDTO requestParam){
        log.info("开启下载任务");
        log.info("开始调用服务");
        minioServiceClient.upload(requestParam);
        log.info("服务调用完毕");
    }

}
