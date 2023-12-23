package com.niiish32x.lithefs.tools;


import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInit {
    private final MinioProperties minIOProperties;
    public MinioClient init(){
        System.out.println(minIOProperties.getEndpoint());
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minIOProperties.getEndpoint())
                        .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                        .build();

        return minioClient;
    }
}
