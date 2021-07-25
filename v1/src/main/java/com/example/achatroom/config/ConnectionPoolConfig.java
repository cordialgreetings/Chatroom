package com.example.achatroom.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Configuration
public class ConnectionPoolConfig {
    @Bean
    ConnectionPool connectionPool(){
        return new ConnectionPool(ConnectionPoolConfiguration.builder(
                ConnectionFactories.get("r2dbcs:mysql://root:950801@localhost:3306/chatroom"))
                .maxAcquireTime(Duration.ofMillis(1000))
                .build());
    }

    @Bean
    Mono<Connection> connectionMono(){
        return connectionPool().create();
    }

    @Bean
    Scheduler boundedElastic(){
        return Schedulers.boundedElastic();
    }
}
