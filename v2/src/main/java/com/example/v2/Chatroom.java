package com.example.v2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.example.v2.DAO"})
public class Chatroom {

    public static void main(String[] args) {
        SpringApplication.run(Chatroom.class, args);
    }

}
