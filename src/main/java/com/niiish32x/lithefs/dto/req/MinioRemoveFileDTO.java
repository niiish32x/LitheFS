package com.niiish32x.lithefs.dto.req;

import lombok.Data;

@Data
public class MinioRemoveFileDTO {
    String bucketName;
    String objectName;
}
