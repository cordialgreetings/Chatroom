package com.example.achatroom.handler;

import com.example.achatroom.PO.UserPO;
import com.example.achatroom.Repository.UserRepository;
import com.example.achatroom.component.JwtAuthComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Service
public class UserHandler {

    private final JwtAuthComponent jwtAuthComponent;
    private final UserRepository userRepository;

    @Autowired
    public UserHandler(JwtAuthComponent jwtAuthComponent, UserRepository userRepository) {
        this.jwtAuthComponent = jwtAuthComponent;
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserPO.class)
                .flatMap(userRepository::createUser)
                .flatMap(body -> ServerResponse.status(HttpStatus.OK).build());
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParams())
                .flatMap(body->{
                    String username=body.getFirst("username");
                    String password=body.getFirst("password");
                    return userRepository.login(username, password)
                            .doOnNext(System.out::println);
//                            .map(pwd-> pwd.equals(password));
                })
                .flatMap(validated->validated
                        ?ServerResponse.status(HttpStatus.OK).build()
                        :ServerResponse.status(HttpStatus.BAD_REQUEST).build());
    }

    public Mono<ServerResponse> getUserInfo(ServerRequest serverRequest) {
        return ServerResponse.ok().body(Mono.just(serverRequest.pathVariable("username")), String.class);
    }
}
