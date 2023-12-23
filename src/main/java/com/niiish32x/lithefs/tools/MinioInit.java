package com.niiish32x.lithefs.tools;


import com.niiish32x.lithefs.common.config.MinIOConfig;
import io.minio.MinioClient;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInit {
    private final  MinIOConfig minIOConfig;
    public MinioClient init(){
        System.out.println(minIOConfig.getEndpoint());
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minIOConfig.getEndpoint())
                        .credentials(minIOConfig.getAccessKey(), minIOConfig.getSecretKey())
                        .build();

        return minioClient;
    }
}
