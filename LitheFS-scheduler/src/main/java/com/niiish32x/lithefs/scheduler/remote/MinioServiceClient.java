package com.niiish32x.lithefs.scheduler.remote;

import com.niiish32x.lithefs.scheduler.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.scheduler.dto.req.MinioUploadReqDTO;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "core",path = "/api/minio")
public interface MinioServiceClient {

    @PostMapping("/downloadOverwrite")
    void download(@RequestBody MinioDownloadReqDTO requestParam);

    @PostMapping("/upload")
    void upload(@RequestBody MinioUploadReqDTO requestParam);
}
