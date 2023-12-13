package com.niiish32x.lithefs.service.impl;

import com.niiish32x.lithefs.common.result.Result;
import com.niiish32x.lithefs.service.SysUploaderService;
import com.niiish32x.lithefs.tools.MinioInit;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Service
@RequiredArgsConstructor
public class SysUploaderServiceImpl implements SysUploaderService {


    private final MinioInit minioInit;


    @Override
    public void uploadFile(String bucketName, String objectName, String fileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = minioInit.init();;

        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(fileName)
                            .build()
            );
        }catch (MinioException e){
            System.out.println("Error occurred:" + e);
            System.out.println("HTTP trace " + e.httpTrace());
        }


    }

}
