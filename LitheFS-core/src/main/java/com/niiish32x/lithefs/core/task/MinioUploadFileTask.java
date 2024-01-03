package com.niiish32x.lithefs.core.task;

import com.niiish32x.lithefs.core.dto.req.MinioUploadReqDTO;
import com.niiish32x.lithefs.core.service.MinioFileService;
import com.niiish32x.lithefs.core.tools.MinioInit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioUploadFileTask {
    private final MinioFileService minioFileService;
    private final MinioInit minioInit;

    /**
     * 定时上传: 上传 文件 或 文件夹下所有内容 至 对象的桶
     */
    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    @SneakyThrows
    public void uploadFileTask(){

        // 根据实际需求进行修改
        String folderPath = "./tmp/taskUpload";
        String bucketName = "taskUpload";

        log.info(LocalDateTime.now() +  ": 开始定时任务 上传文件夹 "+ folderPath + "下的所有文件至目标桶 " + bucketName);

        File folder = new File(folderPath);

        upLoadHelper(folder,bucketName);


    }

    public void upLoadHelper(File file,String bucketName){
        if (file == null || !file.exists()){
            log.error("文件" + file.getName() + "不存在 停止上传");
            return;
        }

        if (file.isFile()){
            // 如果为文件 那么直接上传
            minioFileService.uploadFile(new MinioUploadReqDTO(
                    bucketName,file.getName(), file.getPath()
            ));
        }else if (file.isDirectory()){
            // 如果是文件夹 递归处理里面的所有文件
            File [] files = file.listFiles();
            for(File f : files){
                upLoadHelper(f,bucketName);
            }
        }
    }

}
