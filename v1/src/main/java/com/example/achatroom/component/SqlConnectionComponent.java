package com.example.achatroom.component;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SqlConnectionComponent {
    private final ConnectionPool connectionPool;
    private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    @Autowired
    public SqlConnectionComponent(ConnectionPool connectionPool, ReactiveRedisConnectionFactory reactiveRedisConnectionFactory){
        this.connectionPool = connectionPool;
        this.reactiveRedisConnectionFactory = reactiveRedisConnectionFactory;
    }

    @Bean
    Mono<Connection> connectionMysql(){
        return connectionPool.create();
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(){
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

}
