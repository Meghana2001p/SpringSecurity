package com.example.jwtsecurity.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private String email;
    private String password;
  //  private String role;
    private String name;

}
