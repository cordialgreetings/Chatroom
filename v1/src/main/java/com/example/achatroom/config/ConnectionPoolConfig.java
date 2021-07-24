package com.example.achatroom.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionPoolConfig {
    @Bean
    ConnectionPool connectionPool(){
        return new ConnectionPool(ConnectionPoolConfiguration.builder(
                ConnectionFactories.get("r2dbc:pool:mysql://root:950801@localhost:3306/chatroom"))
                .build());
    }
}
