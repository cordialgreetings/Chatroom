package com.example.achatroom.component;

import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KeyGenerator {
    public RedisAtomicInteger key;
    public static final String GENERATOR_KEY = "GENERATOR_KEY";
    public static final String INIT_KEY = "select max(`roomId`) from `room`";

    @Autowired
    public KeyGenerator(RedisConnectionFactory redisConnectionFactory, Mono<Connection> connectionMysql) {
        connectionMysql.flatMap(connection -> Mono.from(connection.createStatement(INIT_KEY).execute())
                .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()))
                .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Integer.class))))
                .onErrorReturn(0)
                .doOnNext(i -> key=new RedisAtomicInteger(GENERATOR_KEY, redisConnectionFactory, i))
                .subscribe();
    }

    public int generate(){
        return key.incrementAndGet();
    }
}
