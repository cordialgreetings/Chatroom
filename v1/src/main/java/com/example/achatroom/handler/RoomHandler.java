package com.example.achatroom.handler;

import com.example.achatroom.Repository.RoomRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class RoomHandler {
    private final RoomRepository roomRepository;
    private final ServerResponse.BodyBuilder okResponseBuilder;
    private final Mono<ServerResponse> badResponse;
    private final Mono<ServerResponse> okResponse;

    public RoomHandler(RoomRepository roomRepository, ServerResponse.BodyBuilder okResponseBuilder, Mono<ServerResponse> badResponse, Mono<ServerResponse> okResponse) {
        this.roomRepository = roomRepository;
        this.okResponseBuilder = okResponseBuilder;
        this.badResponse = badResponse;
        this.okResponse = okResponse;
    }

    @NotNull
    public Mono<ServerResponse> createRoom(ServerRequest serverRequest){
        return serverRequest.bodyToMono(String.class)
                .flatMap(s -> okResponseBuilder.body(roomRepository.createRoom(s),String.class))
                .switchIfEmpty(badResponse);
    }

    @NotNull
    public Mono<ServerResponse> enterRoom(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    @NotNull
    public Mono<ServerResponse> leaveRoom(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    @NotNull
    public Mono<ServerResponse> getRoomInfo(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    @NotNull
    public Mono<ServerResponse> getUsers(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }

    @NotNull
    public Mono<ServerResponse> getRooms(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }
}
