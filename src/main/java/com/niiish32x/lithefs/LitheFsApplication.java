package com.niiish32x.lithefs;


import org.apache.ibatis.annotations.Mapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
//@MapperScan("com.niiish32x.lithefs.dao.mapper")

// 这个必须是指定具体的mapper报名才可以顺利 否则就会报错
@MapperScan("com.niiish32x.lithefs.dao.mapper")
@SpringBootApplication
public class LitheFsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LitheFsApplication.class, args);
	}

}
