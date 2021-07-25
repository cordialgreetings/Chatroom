package com.example.achatroom.Repository;

import com.example.achatroom.PO.UserPO;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


@Repository
public class UserRepository {
    public final Mono<Connection> connectionMono;
    public final Scheduler scheduler;
    public static final String loginSQl="select password from user where username=?";
    public static final String createUserSQL = "replace into `user` values (?,?,?,?,?,?)";

    @Autowired
    public UserRepository(Mono<Connection> connectionMono, Scheduler boundedElastic) {
        this.connectionMono=connectionMono;
        this.scheduler = boundedElastic;
    }

    public Mono<Void> createUser(UserPO userPO) {
        return connectionMono.flatMap(connection ->
                Mono.from(connection.createStatement(createUserSQL)
                        .bind(0, userPO.getUsername()).bind(1, userPO.getFirstName())
                        .bind(2, userPO.getLastName()).bind(3, userPO.getEmail())
                        .bind(4, userPO.getPassword()).bind(5, userPO.getPhone())
                        .execute()
                    ).doFinally(signalType -> Mono.from(connection.close()).subscribe())
                ).subscribeOn(scheduler).then();
    }

    public Mono<String> login(String username) {
        return connectionMono.flatMap(connection ->
                Mono.from(connection.createStatement(loginSQl)
                        .bind(0, username).execute())
                        .doFinally(signalType -> Mono.from(connection.close()).subscribe()))
                .flatMap(result -> Mono.from(result.map(((row, rowMetadata) -> row.get(0, String.class)))))
                .subscribeOn(scheduler);
    }

}
