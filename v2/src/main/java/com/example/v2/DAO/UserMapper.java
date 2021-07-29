package com.example.v2.DAO;

import com.example.v2.PO.UserPO;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    void register(UserPO userPO);
    String login(String username);
}
