package com.example.achatroom.handler;

import com.example.achatroom.PO.UserPO;
import com.example.achatroom.Repository.UserRepository;
import com.example.achatroom.component.JwtAuthComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class UserHandler {

    private final JwtAuthComponent jwtAuthComponent;
    private final UserRepository userRepository;

    @Autowired
    public UserHandler(JwtAuthComponent jwtAuthComponent, UserRepository userRepository) {
        this.jwtAuthComponent = jwtAuthComponent;
        this.userRepository = userRepository;
    }

    @NotNull
    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserPO.class)
                .flatMap(userRepository::createUser)
                .flatMap(body -> ServerResponse.status(HttpStatus.OK).build());
    }

    @NotNull
    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParams())
                .flatMap(body -> {
                    List<String> list = body.get("username");
                    if(list == null || list.isEmpty()){
                        return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
                    }
                    String username=list.get(0);
                    list = body.get("password");
                    if(list == null || list.isEmpty()){
                        return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
                    }
                    String password = list.get(0);
                    return userRepository.login(username).flatMap(pwd -> pwd.equals(password)
                            ? ServerResponse.status(HttpStatus.OK).body(jwtAuthComponent.getToken(Mono.just(username)),String.class)
                            : ServerResponse.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @NotNull
    public Mono<ServerResponse> getUserInfo(ServerRequest serverRequest) {
        return ServerResponse.ok().body(Mono.just(serverRequest.pathVariable("username")), String.class);
    }
}
