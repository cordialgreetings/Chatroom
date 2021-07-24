package com.example.achatroom.PO;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table(value="User")
public class UserPO implements Serializable {
    @Id
    String username;
    @Column
    String firstName;
    @Column
    String lastName;
    @Column
    String email;
    @Column
    String password;
    @Column
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
