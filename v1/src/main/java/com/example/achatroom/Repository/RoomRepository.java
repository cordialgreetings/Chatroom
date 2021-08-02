package com.example.achatroom.Repository;

import com.example.achatroom.component.KeyGenerator;
import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class RoomRepository {
    public final Mono<Connection> connectionMysql;
    public final ReactiveStringRedisTemplate redisTemplate;
    public final String CREATE_ROOM_SQL = "insert into `room`(`name`) values (?)";
    @Autowired
    public RoomRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.connectionMysql = connectionMysql;
        this.redisTemplate = reactiveStringRedisTemplate;
    }

    public Mono<String> createRoom(String name){
        return connectionMysql.flatMap(
                connection -> Mono.from(connection.createStatement(CREATE_ROOM_SQL)
                        .bind(0, name).returnGeneratedValues("roomId").execute())
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()))
                .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Integer.class))))
                .map(Object::toString);
    }

}
