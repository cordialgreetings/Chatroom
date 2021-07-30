package com.example.achatroom.config;

import com.example.achatroom.filter.BearerTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import com.example.achatroom.handler.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    private final BearerTokenFilter bearerTokenFilter;
    private final MessageHandler messageHandler;
    private final RoomHandler roomHandler;
    private final UserHandler userHandler;

    @Autowired
    public RouterFunctionConfig(BearerTokenFilter bearerTokenFilter, MessageHandler messageHandler, RoomHandler roomHandler, UserHandler userHandler) {
        this.bearerTokenFilter = bearerTokenFilter;
        this.messageHandler = messageHandler;
        this.roomHandler = roomHandler;
        this.userHandler = userHandler;
    }

    @Bean
    public RouterFunction<?> MessageRouter() {
        return route(POST("/message/send"), messageHandler::send)
                .andRoute(POST("/message/retrieve"), messageHandler::retrieve)
                .filter(bearerTokenFilter);
    }

    @Bean
    public RouterFunction<?> RoomRouter(){
        return route(POST("/room"), roomHandler::createRoom)
                .andRoute(PUT("/room/{roomId}/enter"), roomHandler::enterRoom)
                .andRoute(PUT("/roomLeave"), roomHandler::leaveRoom)
                .filter(bearerTokenFilter)
                .andRoute(GET("/room/{roomId}"), roomHandler::getRoomInfo)
                .andRoute(GET("/room/{roomId}/users"), roomHandler::getUsers)
                .andRoute(POST("/roomList"), roomHandler::getRooms);
    }

    @Bean
    public RouterFunction<?> UserRouter(){
        return route(GET("/user/{username}"), userHandler::getUserInfo)
                .andRoute(POST("/user"), userHandler::createUser)
                .andRoute(GET("/userLogin"), userHandler::login);
    }
}
