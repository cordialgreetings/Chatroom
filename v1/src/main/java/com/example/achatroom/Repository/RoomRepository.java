package com.example.achatroom.Repository;

import com.example.achatroom.BO.PageBO;
import com.example.achatroom.BO.RoomBO;
import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class RoomRepository {
    public final Mono<Connection> connectionMysql;
    public final ReactiveStringRedisTemplate redisTemplate;
    public static final String CREATE_ROOM_SQL = "insert into `room`(`name`) values (?)";
    public static final String GET_ROOM_INFO_SQL = "select `name` from `room` where roomId=?";
    public static final String GET_USERS_SQL = "select `username` from `roomuser` where roomId=?";
    public static final String GET_ROOMS_SQL = "select * from `room` where ?<roomId limit ?";
    @Autowired
    public RoomRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.connectionMysql = connectionMysql;
        this.redisTemplate = reactiveStringRedisTemplate;
    }

    public Mono<String> createRoom(String name){
        return connectionMysql.flatMap(connection ->
                Mono.from(connection.createStatement(CREATE_ROOM_SQL)
                        .bind(0, name).returnGeneratedValues("roomId").execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Integer.class))))
                        .map(Object::toString)
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

    public Mono<String> getRoomInfo(int roomId){
        return connectionMysql.flatMap(connection ->
                Mono.from(connection.createStatement(GET_ROOM_INFO_SQL)
                        .bind(0, roomId).execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, String.class))))
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

    public Mono<List<String>> getUsers(int roomId){
        return connectionMysql.flatMap(connection ->
                Flux.from(connection.createStatement(GET_USERS_SQL)
                        .bind(0, roomId).execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, String.class))))
                        .collectList()
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

    public Mono<List<RoomBO>> getRooms(PageBO pageBO){
        return connectionMysql.flatMap(connection ->
                Flux.from(connection.createStatement(GET_ROOMS_SQL)
                        .bind(0, pageBO.getPageIndex()* pageBO.getPageSize())
                        .bind(1, pageBO.getPageSize()).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                new RoomBO(row.get(1,String.class),row.get(0,Integer.class).toString())))
                        .collectList()
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

}
