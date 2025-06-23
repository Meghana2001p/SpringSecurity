package com.example.jwtsecurity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserResponseDTO {

        private String email;
        private String role;
        private String name;
        private Integer id;
        private String token;
    }


