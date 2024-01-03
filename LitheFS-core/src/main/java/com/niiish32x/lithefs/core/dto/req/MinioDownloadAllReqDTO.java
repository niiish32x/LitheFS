package com.niiish32x.lithefs.core.dto.req;

import lombok.Data;

/**
 * Minio请求下载实体 下载一个bucket下的所有文件
 */
@Data
public class MinioDownloadAllReqDTO {
    String bucketName;
    String downloadPath;
}
