package com.example.achatroom.handler;

import com.example.achatroom.BO.PageBO;
import com.example.achatroom.BO.RoomNameBO;
import com.example.achatroom.BO.RoomBO;
import com.example.achatroom.Repository.RoomRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RoomHandler {
    private final RoomRepository roomRepository;
    private final ServerResponse.BodyBuilder okResponseBuilder;
    private final Mono<ServerResponse> badResponse;
    private final Mono<ServerResponse> okResponse;
    private final Mono<RoomBO> error = Mono.error(new IllegalArgumentException());

    public RoomHandler(RoomRepository roomRepository, ServerResponse.BodyBuilder okResponseBuilder, Mono<ServerResponse> badResponse, Mono<ServerResponse> okResponse) {
        this.roomRepository = roomRepository;
        this.okResponseBuilder = okResponseBuilder;
        this.badResponse = badResponse;
        this.okResponse = okResponse;
    }

    @NotNull
    public Mono<ServerResponse> createRoom(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMono(RoomNameBO.class))
                .flatMap(roomNameBO -> okResponseBuilder.body(roomRepository.createRoom(roomNameBO.name), String.class))
                .switchIfEmpty(badResponse);
    }

    @NotNull
    public Mono<ServerResponse> enterRoom(ServerRequest serverRequest) {
        List<String> list = serverRequest.headers().header("name");
        if (list.isEmpty()) {
            return badResponse;
        }
        String username = list.get(0);
        int roomId = Integer.parseInt(serverRequest.pathVariable("roomId"), 10);
        return roomRepository.enterRoom(username, roomId).flatMap(aBool -> aBool ? okResponse : badResponse);
    }

    @NotNull
    public Mono<ServerResponse> leaveRoom(ServerRequest serverRequest) {
        List<String> list = serverRequest.headers().header("name");
        if (list.isEmpty()) {
            return badResponse;
        }
        String username = list.get(0);
        return roomRepository.leaveRoom(username).flatMap(aBool -> aBool ? okResponse : badResponse);
    }

    @NotNull
    public Mono<ServerResponse> getRoomInfo(ServerRequest serverRequest) {
        Mono<String> name = roomRepository.getRoomInfo(Integer.parseInt(
                serverRequest.pathVariable("roomId"), 10));
        return name.flatMap(s -> okResponseBuilder.body(name, String.class))
                .switchIfEmpty(badResponse);
    }

    @NotNull
    public Mono<ServerResponse> getUsers(ServerRequest serverRequest) {
        return okResponseBuilder.body(roomRepository.getUsers(Integer.parseInt(serverRequest.pathVariable("roomId"), 10)), String.class);
    }

    @NotNull
    public Mono<ServerResponse> getRooms(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PageBO.class)
                .flatMap(pageBO -> pageBO.pageIndex>=0
                        ?okResponseBuilder.body(roomRepository.getRooms(pageBO)
                            .switchIfEmpty(error),RoomBO.class)
                        :badResponse
                );
//        reactor的反人类之处,flux/mono对象不会被修改，但里面的数据流留出去之后就没了，然后我判断一下有没有元素，然后里面的元素也没了
//        return rooms.hasElements().flatMap(aBool -> aBool?okResponseBuilder.body(rooms,RoomBO.class):badResponse);
    }
}
