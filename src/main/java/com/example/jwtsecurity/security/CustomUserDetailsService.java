package com.example.jwtsecurity.security;

import com.example.jwtsecurity.Entity.User;

import com.example.jwtsecurity.Repository.UserRepository;
import com.example.jwtsecurity.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


//UserDetails is a standard way the Spring security is going to understand and it has some important methods
//Your actual user class (maybe called User, Account, etc.) can be anything with any fields
// UserDetails provides a common contract so Spring Security can work with any user model without depending on your exact class.


@Service
public class CustomUserDetailsService implements UserDetailsService {

//we need the userRepository for this
     @Autowired
    private UserRepository repo;


    //custom inbuilt method of this interface
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("Email  for loading the data from the database  " + email);
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

//now converting the custom user object to the SpringUserDetails object
//the first line builds the userDetails object using the Spring Security
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
     //   public String createToken(Long id, String name, String role, String email) {

        return new UserDetailsImpl(user.getId(),user.getName(),user.getEmail(),user.getRole(), user.getPassword());

    }
}
