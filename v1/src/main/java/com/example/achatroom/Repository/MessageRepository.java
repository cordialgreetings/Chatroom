package com.example.achatroom.Repository;

import com.example.achatroom.BO.MessageBO;
import com.example.achatroom.BO.PageBO;
import com.example.achatroom.PO.MessagePO;
import io.r2dbc.spi.Connection;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class MessageRepository {
    public final Mono<Connection> connectionMysql;
    public final ReactiveHashOperations<String,String,String> reactiveHashOperations;
    public final Mono<Boolean> trueMono = Mono.just(true);
    public final Mono<Boolean> falseMono = Mono.just(false);
    public final Flux<MessagePO> errorFlux = Flux.error(new IllegalArgumentException());
    public static final String USER2ROOM = "u2r";
    public static final String SEND_SQL = "insert into `message` values (?,?,?,?)";
    public static final String RETRIEVE_SQL = "select `timestamp`,`messageId`,`text` from `message` where `roomId`=? order by `timestamp` desc limit ?,?";

    public MessageRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate redisTemplate) {
        this.connectionMysql = connectionMysql;
        this.reactiveHashOperations = redisTemplate.opsForHash();
    }

    public Mono<Boolean> send(String username, MessageBO messageBO) {
        long timestamp = System.currentTimeMillis();
        return reactiveHashOperations.get(USER2ROOM, username)
                .defaultIfEmpty("-1")
                .map(i -> Integer.parseInt(i, 10))
                .flatMap(roomId -> {
                    if (roomId == -1) {
                        return falseMono;
                    } else {
                        return connectionMysql.flatMap(connection ->
                                Mono.from(connection.createStatement(SEND_SQL)
                                        .bind(0, roomId).bind(1, timestamp)
                                        .bind(2, messageBO.id)
                                        .bind(3, messageBO.text).execute())
                                        .doFinally(signalType -> ((Mono<Void>) connection.close()).subscribe()))
                                .then(trueMono).onErrorResume(throwable -> falseMono);
                    }
                });
    }

    public Flux<MessagePO> retrive(String username, PageBO pageBO) {
        return reactiveHashOperations.get(USER2ROOM, username)
                .defaultIfEmpty("-1")
                .map(i -> Integer.parseInt(i, 10))
                .flatMapMany(roomId -> {
                    if (roomId == -1) {
                        return errorFlux;
                    } else {
                        return connectionMysql.flatMapMany(connection ->
                                Flux.from(connection.createStatement(RETRIEVE_SQL)
                                        .bind(0, roomId)
                                        .bind(1, (~pageBO.pageIndex) * pageBO.getPageSize())
                                        .bind(2, pageBO.getPageSize())
                                        .execute()
                                ).doFinally(signalType -> ((Mono<Void>) connection.close()).subscribe())
                        ).flatMap(result -> result.map((row, rowMetadata) ->
                                        new MessagePO(row.get(1, String.class), row.get(2, String.class),
                                                row.get(0, Long.class).toString()))
                        );
                    }
                });
    }
}
