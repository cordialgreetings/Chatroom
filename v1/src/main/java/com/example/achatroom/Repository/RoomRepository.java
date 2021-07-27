package com.example.achatroom.Repository;

import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoomRepository {
    public final Mono<Connection> connectionMono;
    public final String CREATE_ROOM_SQL = "insert into `room`(name) values (?)";
    @Autowired
    public RoomRepository(Mono<Connection> connectionMono) {
        this.connectionMono = connectionMono;
    }

    public Mono<String> createRoom(String name){
        return connectionMono.flatMap(connection ->
                Mono.from(connection.createStatement(CREATE_ROOM_SQL)
                        .bind(0,name).returnGeneratedValues("roomId").execute())
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()))
                .flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        row.get(0, Integer.class))).map(Object::toString));
    }

}
