package com.niiish32x.lithefs.scheduler.service;

import com.niiish32x.lithefs.scheduler.dto.req.MinioDownloadReqDTO;

public interface MinioTaskService {
    void startDownloadFileTask(MinioDownloadReqDTO requestParam);
}
