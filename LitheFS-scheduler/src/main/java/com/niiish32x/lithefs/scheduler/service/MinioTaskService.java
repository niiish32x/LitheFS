package com.niiish32x.lithefs.scheduler.service;

import com.niiish32x.lithefs.scheduler.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.scheduler.dto.req.MinioUploadReqDTO;

public interface MinioTaskService {
    void startDownloadFileTask(MinioDownloadReqDTO requestParam);

    void startUploadFileTask(MinioUploadReqDTO requestParam);
}
