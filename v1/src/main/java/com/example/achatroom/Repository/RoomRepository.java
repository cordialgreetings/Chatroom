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
    public final KeyGenerator keyGenerator;
    public final String CREATE_ROOM_SQL = "insert into `room`(`roomId`,`name`) values (?, ?)";
    @Autowired
    public RoomRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate reactiveStringRedisTemplate, KeyGenerator keyGenerator) {
        this.connectionMysql = connectionMysql;
        this.redisTemplate = reactiveStringRedisTemplate;
        this.keyGenerator = keyGenerator;
    }

    public Mono<String> createRoom(String name){
        Mono<Integer> key = Mono.just(keyGenerator.generate());
        return key.flatMap(i -> connectionMysql.flatMap(
                connection -> Mono.from(connection.createStatement(CREATE_ROOM_SQL)
                        .bind(0, i).bind(1, name).execute())
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()))
        ).then(key).map(Object::toString);
    }

}
