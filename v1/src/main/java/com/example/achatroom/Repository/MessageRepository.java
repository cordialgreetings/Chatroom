package com.example.achatroom.Repository;

import com.example.achatroom.BO.MessageSendBO;
import com.example.achatroom.BO.PageBO;
import com.example.achatroom.PO.MessagePO;
import io.r2dbc.spi.Connection;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class MessageRepository {
    public final Mono<Connection> connectionMysql;
    public final ReactiveStringRedisTemplate redisTemplate;
    public final Mono<Boolean> trueMono = Mono.just(true);
    public final Mono<Boolean> falseMono = Mono.just(false);
    public final Mono<String> initRoomIdMono = Mono.just("-1");
    public static final String USER2ROOM = "u2r";
    public static final String SEND_SQL = "insert into `message` values (?,?,?,?)";
    public static final String RETRIEVE_SQL = "select `timestamp`,`messageId`,`text` from `message` where `roomId`=? order by `timestamp` desc limit ?,?";

    public MessageRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate redisTemplate) {
        this.connectionMysql = connectionMysql;
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> send(String username, MessageSendBO messageSendBO) {
        long timestamp = System.currentTimeMillis();
        return redisTemplate.opsForHash().get(USER2ROOM, username)
                .switchIfEmpty(initRoomIdMono)
                .map(i -> Integer.parseInt((String) i, 10))
                .flatMap(roomId -> {
                    if (roomId == -1) {
                        return falseMono;
                    } else {
                        return connectionMysql.flatMap(connection ->
                                Mono.from(connection.createStatement(SEND_SQL)
                                        .bind(0, roomId).bind(1, timestamp)
                                        .bind(2, messageSendBO.getId())
                                        .bind(3, messageSendBO.getText()).execute())
                                        .doFinally(signalType -> ((Mono<Void>) connection.close()).subscribe()))
                                .then(trueMono).onErrorResume(throwable -> falseMono);
                    }
                });
    }

    public Mono<List<MessagePO>> retrive(String username, PageBO pageBO) {
        return redisTemplate.opsForHash().get(USER2ROOM, username)
                .switchIfEmpty(initRoomIdMono)
                .map(i -> Integer.parseInt((String) i, 10))
                .flatMap(roomId -> {
                    if (roomId == -1) {
                        return Mono.empty();
                    } else {
                        return connectionMysql.flatMapMany(connection ->
                                Flux.from(connection.createStatement(RETRIEVE_SQL)
                                        .bind(0, roomId)
                                        .bind(1, (~pageBO.getPageIndex()) * pageBO.getPageSize())
                                        .bind(2, pageBO.getPageSize())
                                        .execute()
                                ).doFinally(signalType -> ((Mono<Void>) connection.close()).subscribe())
                        ).flatMap(result -> result.map((row, rowMetadata) ->
                                        new MessagePO(row.get(1, String.class), row.get(2, String.class),
                                                row.get(0, Long.class).toString()))
                        ).collectList();
                    }
                });
    }
}
