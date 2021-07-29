package com.example.v2.Controller;

import com.example.v2.DAO.UserMapper;
import com.example.v2.PO.UserPO;
import com.example.v2.component.JwtAuthComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    public final UserMapper userMapper;
    public final JwtAuthComponent jwtAuthComponent;
    public final ResponseEntity.BodyBuilder okResponseBuilder;
    public final ResponseEntity<Object> okResponse;
    public final ResponseEntity<Object> badResponse;

    @Autowired
    public UserController(UserMapper userMapper, JwtAuthComponent jwtAuthComponent, ResponseEntity.BodyBuilder okResponseBuilder, ResponseEntity<Object> okResponse, ResponseEntity<Object> badResponse) {
        this.userMapper = userMapper;
        this.jwtAuthComponent = jwtAuthComponent;
        this.okResponseBuilder = okResponseBuilder;
        this.okResponse = okResponse;
        this.badResponse = badResponse;
    }

    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody UserPO userPO){
        userMapper.register(userPO);
        return okResponse;
    }

    @GetMapping(value = "/userLogin", produces = "application/json")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password){
        if(username == null || password == null){
            return badResponse;
        }
        return password.equals(userMapper.login(username))
                ?okResponseBuilder.body(jwtAuthComponent.getToken(username))
                :badResponse;
    }

}
