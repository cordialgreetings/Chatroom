package com.example.achatroom.BO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBO {
    String firstName;
    String lastName;
    String email;
    String phone;
}
