package com.example.achatroom.PO;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserPO implements Serializable {
    String username;
    String firstName;
    String lastName;
    String email;
    String password;
    String phone;
}
