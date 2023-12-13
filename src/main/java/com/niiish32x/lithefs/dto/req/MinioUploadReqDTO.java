package com.niiish32x.lithefs.dto.req;

import lombok.Data;


/**
 * Minio 上传参数请求实体
 */
@Data
public class MinioUploadReqDTO {
    String bucketName;
    String objectName;
    String uploadFileName;
}
