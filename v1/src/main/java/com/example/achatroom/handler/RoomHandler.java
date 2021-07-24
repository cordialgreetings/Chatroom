package com.example.achatroom.handler;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class RoomHandler {
    public Mono<ServerResponse> createRoom(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    public Mono<ServerResponse> enterRoom(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    public Mono<ServerResponse> leaveRoom(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    public Mono<ServerResponse> getRoomInfo(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    public Mono<ServerResponse> getUsers(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    public Mono<ServerResponse> getRooms(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }
}
