package com.niiish32x.lithefs.core.task;

import com.niiish32x.lithefs.core.service.MinioFileService;
import com.niiish32x.lithefs.core.tools.MinioInit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioDownloadTask {
    private final MinioFileService minioFileService;
    private final MinioInit minioInit;

    @Value(value = "${custom.minioListenedBucket}")
    private String listenedBucket;


    /**
     * 定时任务
     * 定时从Minio指定的bucket 下载文件 文件至本地指定的文件夹
     */
//    @SneakyThrows
//    @Scheduled(cron = "0 */1 * * * ?")  // 每分钟从minio指定的bucket下载文件到本地的文件夹
//    public void download(){
//        System.out.println(listenedBucket);
//        Path path = Paths.get("./tmp/download");
//        minioFileService.downloadAllFileOverwrite(listenedBucket,path.toString());
//    }
}
