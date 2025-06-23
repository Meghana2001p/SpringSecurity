package com.example.jwtsecurity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment in MySQL

private Integer id;
    private String name;
   // @JsonIgnore
    //if I am going to use this annotation I will nor be able to send the password
    private String password;//1234
    private String role;
    private String email;
}
