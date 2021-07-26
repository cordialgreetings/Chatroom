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
                ConnectionFactories.get(ConnectionFactoryOptions.builder()
                        .option(Option.valueOf("driver"), "mysql")
                        .option(Option.valueOf("host"),"127.0.0.1")
                        .option(Option.valueOf("user"), "root")
//                        .option(Option.valueOf("port"),"3306")
                        .option(Option.valueOf("password"),"950801")
                        .option(Option.valueOf("database"),"chatroom")
//                        .option(Option.valueOf("connect_timeout"),Duration.ofSeconds(3))
                        .option(Option.valueOf("tcpKeepAlive"), true) // optional, default false
//                        .option(Option.valueOf("tcpNoDelay"), true) // optional, default false
                        .build()))
                .maxAcquireTime(Duration.ofSeconds(6))
                .build());
    }

//    @Bean
//    ConnectionPool connectionPool(){
//        return new ConnectionPool(ConnectionPoolConfiguration.builder(
//                ConnectionFactories.get("r2dbcs:mysql://root:950801@localhost:3306/chatroom"))
//                .maxAcquireTime(Duration.ofSeconds(6)).build());
//    }

    @Bean
    Mono<Connection> connectionMono(){
        return connectionPool().create();
    }

    @Bean
    Scheduler boundedElastic(){
        return Schedulers.boundedElastic();
    }
}
