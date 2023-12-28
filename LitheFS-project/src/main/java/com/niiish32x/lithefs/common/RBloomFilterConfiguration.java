package com.niiish32x.lithefs.common;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> minioUploadFileBloomFilter(RedissonClient redissonClient){
        RBloomFilter<String> rBloomFilter = redissonClient.getBloomFilter("minioUploadFileBloomFilter");
        rBloomFilter.tryInit(100000000L, 0.001);
        return rBloomFilter;
    }
}
