package com.example.achatroom.handler;

import com.example.achatroom.BO.MessageBO;
import com.example.achatroom.BO.PageBO;
import com.example.achatroom.PO.MessagePO;
import com.example.achatroom.Repository.MessageRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
        return serverRequest.body(BodyExtractors.toMono(MessageBO.class))
                .flatMap(messageBO -> messageRepository.send(serverRequest.headers().header("name").get(0), messageBO))
                .flatMap(aBool -> aBool?okResponse:badResponse);
    }
    @NotNull
    public Mono<ServerResponse> retrieve(ServerRequest serverRequest){
        return serverRequest.body(BodyExtractors.toMono(PageBO.class))
                .flatMap(pageBO -> pageBO.pageIndex<0
                        ?okResponseBuilder.body(messageRepository.retrive(
                                serverRequest.headers().header("name").get(0), pageBO), MessagePO.class)
                        :badResponse);
    }
}
