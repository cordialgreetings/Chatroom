package com.example.achatroom.filter;

import com.example.achatroom.component.JwtAuthComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BearerTokenFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private final JwtAuthComponent jwtAuthComponent;
    private final Mono<ServerResponse> badResponse;

    @Autowired
    public BearerTokenFilter(JwtAuthComponent jwtAuthComponent, Mono<ServerResponse> badResponse) {
        this.jwtAuthComponent = jwtAuthComponent;
        this.badResponse = badResponse;
    }
    @NotNull
    @Override
    public Mono<ServerResponse> filter(ServerRequest serverRequest, @NotNull HandlerFunction<ServerResponse> handlerFunction) {
        List<String> list = serverRequest.headers().header(HttpHeaders.AUTHORIZATION);
        String bearerToken = list.isEmpty() ? null : list.get(0);
        String username = jwtAuthComponent.validate(bearerToken);
        if(username==null){
            return badResponse;
        }
//        serverRequest.exchange().getRequest().mutate().header("name", username).build();
        HttpHeaders headers = serverRequest.exchange().getRequest().getHeaders();
        headers = HttpHeaders.writableHttpHeaders(headers);
        headers.add("name", username);
        return handlerFunction.handle(serverRequest);
    }
}
