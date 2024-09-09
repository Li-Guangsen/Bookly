package com.lgs.admin.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lgs.admin.backend","com.lgs.common"})
@MapperScan(basePackages = {"com.lgs.common.dao","com.lgs.admin.backend.dao"})
@PropertySource(value = {"classpath*:application.properties"})
public class AdminBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminBackendApplication.class, args);
    }

}
