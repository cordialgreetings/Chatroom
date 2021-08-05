package com.example.achatroom.Repository;

import com.example.achatroom.BO.UserBO;
import com.example.achatroom.PO.UserPO;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepository {
    public final Mono<Connection> connectionMysql;
    public static final String LOGIN_SQL ="select password from `user` where username=?";
    public static final String CREATE_USER_SQL = "replace into `user` values (?,?,?,?,?,?)";
    public static final String GET_USERINFO_SQL = "select firstName, lastName, email, phone from `user` where username=?";

    @Autowired
    public UserRepository(Mono<Connection> connectionMysql) {
        this.connectionMysql =connectionMysql;
    }

    public Mono<Result> createUser(UserPO userPO) {
        return connectionMysql.flatMap(connection ->
                    Mono.from(connection.createStatement(CREATE_USER_SQL)
                        .bind(0, userPO.getUsername()).bind(1, userPO.getFirstName())
                        .bind(2, userPO.getLastName()).bind(3, userPO.getEmail())
                        .bind(4, userPO.getPassword()).bind(5, userPO.getPhone())
                        .execute()
                    ).doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe())
        );
    }

    public Mono<String> login(String username) {
        return connectionMysql.flatMap(
                connection -> Mono.from(connection.createStatement(LOGIN_SQL)
                        .bind(0, username).execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, String.class))))
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

    public Mono<UserBO> getUserInfo(String username){
        return connectionMysql.flatMap(connection ->
                Mono.from(connection.createStatement(GET_USERINFO_SQL)
                        .bind(0, username).execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                                new UserBO(row.get(0, String.class), row.get(1, String.class),
                                        row.get(2, String.class), row.get(3, String.class)))))
                        .doFinally(signalType -> ((Mono<Void>)connection.close()).subscribe()));
    }

}
