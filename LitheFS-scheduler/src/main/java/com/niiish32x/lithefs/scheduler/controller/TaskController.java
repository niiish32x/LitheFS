package com.niiish32x.lithefs.scheduler.controller;

import com.niiish32x.lithefs.scheduler.dto.req.MinioDownloadReqDTO;
import com.niiish32x.lithefs.scheduler.service.impl.MinioTaskServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final MinioTaskServiceImpl minioTaskService;
    @PostMapping("/minioDownload")
    public void minioDownloadTask(@RequestBody MinioDownloadReqDTO requestParam){
        minioTaskService.startDownloadFileTask(requestParam);
    }
}
