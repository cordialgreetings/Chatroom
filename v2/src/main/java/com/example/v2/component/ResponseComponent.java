package com.example.v2.component;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseComponent {
    @Bean
    ResponseEntity.BodyBuilder okResponseBuilder(){
        return ResponseEntity.status(HttpStatus.OK);
    }

    @Bean
    ResponseEntity<Object> okResponse(){
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Bean
    ResponseEntity<Object> badResponse(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
