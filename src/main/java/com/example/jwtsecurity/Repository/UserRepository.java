package com.example.jwtsecurity.Repository;

import com.example.jwtsecurity.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
@Query("select u from User u where u.email= :email")
    Optional<User> findByEmail(@Param("email") String email);
//Param connects the method input to the part of the query

}
