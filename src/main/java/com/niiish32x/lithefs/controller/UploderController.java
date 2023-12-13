package com.niiish32x.lithefs.controller;

import com.niiish32x.lithefs.common.result.Result;
import com.niiish32x.lithefs.service.SysUploaderService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class UploderController {
    private final SysUploaderService sysUploaderService;

    /**
     *
     * @param bucketName  要存储的桶
     * @param objectName  要存储的对象
     * @param fileName    要存储的文件具体路径
     * @return
     */
    @PostMapping("/api/minio/upload")
    public void MinioUpload(@RequestParam("bucketName") String bucketName,@RequestParam("objectName") String objectName, @RequestParam("fileName") String fileName ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        sysUploaderService.uploadFile(bucketName,objectName,fileName);
    }
}
