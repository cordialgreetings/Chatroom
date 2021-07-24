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

    @Override
    public String toString(){
        return "username: "+username+"\n"
                +"firstName: "+firstName+"\n"
                +"lastName: "+lastName+"\n"
                +"email: "+email+"\n"
                +"password: "+password+"\n"
                +"phone: "+phone;
    }
}
