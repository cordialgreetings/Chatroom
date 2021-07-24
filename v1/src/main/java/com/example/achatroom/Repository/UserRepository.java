package com.example.achatroom.Repository;

import com.example.achatroom.PO.UserPO;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@Repository
public class UserRepository {
    private final ConnectionPool connectionPool;

    @Autowired
    public UserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Mono<Void> createUser(UserPO userPO) {
        return connectionPool.create()
                .flatMap(connection ->
                        Mono.from(connection.createStatement("replace into `user` values (?,?,?,?,?,?)")
                                .bind(0, userPO.getUsername()).bind(1, userPO.getFirstName())
                                .bind(2, userPO.getLastName()).bind(3, userPO.getEmail())
                                .bind(4, userPO.getPassword()).bind(5, userPO.getPhone())
                                .execute()))
                .then();
    }

    public Mono<String> login(String username) {
        return connectionPool.create()
                .flatMap(connection -> Mono.from(connection.createStatement("select password from user where username=?")
                        .bind(0, username).execute()))
                .flatMap(body -> Mono.from(body.map(((row, rowMetadata) -> row.get(0, String.class)))));
    }

}
