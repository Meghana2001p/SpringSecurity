package com.example.jwtsecurity.service;

import com.example.jwtsecurity.Entity.User;

import java.util.List;
import java.util.Optional;

public interface ServiceUser {
    User addUser(User u);
    String updateUser(Integer id,User u);
    Optional<User> getOneUser(Integer id);
        List<User> getAllUsers();
    void deleteUser(Integer id);

}
