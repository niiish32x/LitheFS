package com.niiish32x.lithefs.core.common;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 主要用于配置 Redisson
 */

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisIp;

    @Value("${spring.data.redis.port}")
    private String redisPort;
    @Bean
    public RedissonClient redissonClient(){
        // 配置类
        Config config = new Config();
        // 添加redis地址 目前只是添加了单点地址 可根据修改为集群地址
        config.useSingleServer().setAddress("redis://" + redisIp + ":" + redisPort);
        return Redisson.create(config);
    }
}
