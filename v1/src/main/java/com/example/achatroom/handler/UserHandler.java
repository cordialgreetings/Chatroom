package com.example.achatroom.handler;

import com.example.achatroom.BO.UserBO;
import com.example.achatroom.PO.UserPO;
import com.example.achatroom.Repository.UserRepository;
import com.example.achatroom.component.JwtAuthComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class UserHandler {

    private final JwtAuthComponent jwtAuthComponent;
    private final UserRepository userRepository;
    private final ServerResponse.BodyBuilder okResponseBuilder;
    private final Mono<ServerResponse> badResponse;
    private final Mono<ServerResponse> okResponse;

    @Autowired
    public UserHandler(JwtAuthComponent jwtAuthComponent, UserRepository userRepository,Mono<ServerResponse> okResponse,
                       ServerResponse.BodyBuilder okResponseBuilder1, Mono<ServerResponse> badResponse) {
        this.jwtAuthComponent = jwtAuthComponent;
        this.userRepository = userRepository;
        this.okResponseBuilder = okResponseBuilder1;
        this.okResponse = okResponse;
        this.badResponse = badResponse;
    }

    @NotNull
    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserPO.class)
                .flatMap(userRepository::createUser)
                .flatMap(body -> okResponse);
    }

    @NotNull
    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParams())
                .flatMap(body -> {
                    List<String> list = body.get("username");
                    if(list == null || list.isEmpty()){
                        return badResponse;
                    }
                    String username=list.get(0);
                    list = body.get("password");
                    if(list == null || list.isEmpty()){
                        return badResponse;
                    }
                    String password = list.get(0);
                    return userRepository.login(username)
                            .flatMap(pwd -> password.equals(pwd)
                            ? okResponseBuilder.body(jwtAuthComponent.getToken(Mono.just(username)),String.class)
                            : badResponse)
                            .switchIfEmpty(badResponse);
                });
    }

    @NotNull
    public Mono<ServerResponse> getUserInfo(ServerRequest serverRequest) {
        Mono<UserBO> userBOMono = userRepository.getUserInfo(serverRequest.pathVariable("username"));
        return userBOMono.flatMap(userBO -> okResponseBuilder.body(userBOMono, UserBO.class))
                .switchIfEmpty(badResponse);
    }
}
