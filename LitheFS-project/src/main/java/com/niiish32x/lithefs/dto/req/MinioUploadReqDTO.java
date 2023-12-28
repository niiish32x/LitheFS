package com.niiish32x.lithefs.dto.req;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;


/**
 * Minio 上传参数请求实体
 */
@Data
public class MinioUploadReqDTO {
    // 要上传的桶名称
    @NonNull
    String bucketName;
    // 要上传的目标名
    @NonNull
    String objectName;
    // 要上传目标的路径
    @NonNull
    String uploadFileName;
    // 是否使用分片上传
    Boolean isMultiPart;

    @Size(min = 5,message = "minio所支持的最小分片大小为5MB")
    int partSize;

}
