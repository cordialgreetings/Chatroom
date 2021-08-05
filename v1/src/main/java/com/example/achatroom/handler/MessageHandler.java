package com.example.achatroom.handler;

import com.example.achatroom.BO.MessageSendBO;
import com.example.achatroom.Repository.MessageRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MessageHandler {
    private final MessageRepository messageRepository;
    private final ServerResponse.BodyBuilder okResponseBuilder;
    private final Mono<ServerResponse> badResponse;
    private final Mono<ServerResponse> okResponse;
    @Autowired
    public MessageHandler(MessageRepository messageRepository, ServerResponse.BodyBuilder okResponseBuilder, Mono<ServerResponse> badResponse, Mono<ServerResponse> okResponse){
        this.messageRepository = messageRepository;
        this.okResponseBuilder = okResponseBuilder;
        this.badResponse = badResponse;
        this.okResponse = okResponse;
    }
    @NotNull
    public Mono<ServerResponse> send(ServerRequest serverRequest){
        List<String> list = serverRequest.headers().header("name");
        if (list.isEmpty()) {
            return badResponse;
        }
        String username = list.get(0);
        return serverRequest.bodyToMono(MessageSendBO.class)
                .flatMap(messageSendBO -> messageRepository.send(username, messageSendBO))
                .flatMap(aBool -> aBool?okResponse:badResponse);
    }
    @NotNull
    public Mono<ServerResponse> retrieve(ServerRequest serverRequest){
        return ServerResponse.ok().body(Mono.just("null"),String.class);
    }
}
