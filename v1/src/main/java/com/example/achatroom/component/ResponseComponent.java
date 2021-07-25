package com.example.achatroom.component;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ResponseComponent {
    @Bean
    ServerResponse.BodyBuilder okResponseBuilder(){
        return ServerResponse.status(HttpStatus.OK);
    }

    @Bean
    Mono<ServerResponse> okResponse(){
        return ServerResponse.status(HttpStatus.OK).build();
    }

    @Bean
    Mono<ServerResponse> badResponse(){
        return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
    }

}
