package com.example.achatroom.Repository;

import com.example.achatroom.PO.UserPO;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Repository
public class UserRepository {
    private final Mono<Connection> connectionMono;
    public static final String loginSQl="select password from user where username=?";
    public static final String createUserSQL = "replace into `user` values (?,?,?,?,?,?)";

    @Autowired
    public UserRepository(ConnectionPool connectionPool) {
        this.connectionMono=connectionPool.create();
    }

    public Mono<Void> createUser(UserPO userPO) {
        return connectionMono.flatMap(connection ->
                Mono.from(connection.createStatement(createUserSQL)
                        .bind(0, userPO.getUsername()).bind(1, userPO.getFirstName())
                        .bind(2, userPO.getLastName()).bind(3, userPO.getEmail())
                        .bind(4, userPO.getPassword()).bind(5, userPO.getPhone())
                        .execute()
                    ).doFinally(signalType -> Mono.from(connection.close()).subscribe())
                ).then();
    }

    public Mono<String> login(String username) {
        return connectionMono.flatMap(connection ->
                Mono.from(connection.createStatement(loginSQl)
                        .bind(0, username).execute())
                        .doFinally(signalType -> Mono.from(connection.close()).subscribe()))
                .flatMap(body -> Mono.from(body.map(((row, rowMetadata) -> row.get(0, String.class)))));
    }

}
