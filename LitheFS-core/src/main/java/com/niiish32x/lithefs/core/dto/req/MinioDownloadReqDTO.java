package com.niiish32x.lithefs.core.dto.req;

import lombok.Data;

/**
 * Minio 文件下载请求实体
 */
@Data
public class MinioDownloadReqDTO {
    String bucketName;
    String objectName;
    String downloadPath;
}
