package com.example.achatroom.Repository;

import com.example.achatroom.BO.PageBO;
import com.example.achatroom.BO.RoomBO;
import com.example.achatroom.component.KeyGenerator;
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
    public final KeyGenerator keyGenerator;
    public final Mono<Boolean> trueMono = Mono.just(true);
    public final Mono<Boolean> falseMono = Mono.just(false);
    public final Mono<String> initRoomIdMono = Mono.just("-1");
    public static final String USER2ROOM = "u2r";
    public static final String CREATE_ROOM_SQL = "insert into `room`(`roomId`,`name`) values (?,?)";
    public static final String ENTER_ROOM_SQL = "insert into `roomuser`(`roomId`,`username`) values (?, ?)";
    public static final String LEAVE_ROOM_SQL = "delete from `roomuser` where roomId=? and username=?";
    public static final String GET_ROOM_INFO_SQL = "select `name` from `room` where roomId=?";
    public static final String GET_USERS_SQL = "select `username` from `roomuser` where roomId=?";
    public static final String GET_ROOMS_SQL = "select * from `room` where ?<roomId limit ?";
    @Autowired
    public RoomRepository(Mono<Connection> connectionMysql, ReactiveStringRedisTemplate reactiveStringRedisTemplate, KeyGenerator keyGenerator) {
        this.connectionMysql = connectionMysql;
        this.redisTemplate = reactiveStringRedisTemplate;
        this.keyGenerator = keyGenerator;
    }

    public Mono<String> createRoom(String name){
        return connectionMysql.flatMap(connection ->
                Mono.from(connection.createStatement(CREATE_ROOM_SQL)
                        .bind(0, keyGenerator.generate()).bind(1,name)
                        .returnGeneratedValues("roomId").execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Integer.class))))
                        .map(Object::toString)
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

    public Mono<Boolean> enterRoom(String username, int roomId){
        int max_id = keyGenerator.get();
        if(roomId<=0||roomId>max_id){
            return falseMono;
        }
         return redisTemplate.opsForHash().get(USER2ROOM, username)
                 .switchIfEmpty(initRoomIdMono)
                 .map(i->Integer.parseInt((String) i,10))
                 .flatMap(oldRoomId->{
                     if(oldRoomId==roomId){
                         return trueMono;
                     }
                     Mono<?> afterMysql;
                     if(oldRoomId!=-1){
                         afterMysql = connectionMysql.flatMap(connection ->
                                 ((Mono<Void>)connection.beginTransaction())
                                         .then(Mono.from(connection.createStatement(LEAVE_ROOM_SQL)
                                                 .bind(0, oldRoomId).bind(1, username).execute()))
                                         .then(Mono.from(connection.createStatement(ENTER_ROOM_SQL)
                                                 .bind(0, roomId).bind(1, username).execute()))
                                         .then((Mono<Void>)connection.commitTransaction())
                                         .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
                    }else{
                        afterMysql = connectionMysql.flatMap(connection ->
                                Mono.from(connection.createStatement(ENTER_ROOM_SQL)
                                        .bind(0, roomId).bind(1, username).execute())
                                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
                    }
                    return afterMysql.then(redisTemplate.opsForHash().put(USER2ROOM, username, Integer.toString(roomId)))
                            .then(trueMono)
                            .onErrorResume(throwable -> falseMono);
                });
    }

    public Mono<Boolean> leaveRoom(String username,int roomId){
        return Mono.just(true);
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
