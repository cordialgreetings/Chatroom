package com.example.achatroom.handler;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class MessageHandler {
    public Mono<ServerResponse> send(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }
    public Mono<ServerResponse> retrieve(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }
}
