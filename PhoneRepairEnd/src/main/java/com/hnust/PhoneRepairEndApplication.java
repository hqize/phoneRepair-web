package com.hnust;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hnust.mapper")
public class PhoneRepairEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneRepairEndApplication.class, args);
    }

}//撒大大
/**
 * 测试
 */
