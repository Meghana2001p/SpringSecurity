package com.example.jwtsecurity.serviceImpl;

import com.example.jwtsecurity.Entity.User;
import com.example.jwtsecurity.Repository.UserRepository;
import com.example.jwtsecurity.service.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ServiceUserImpl implements ServiceUser {

    @Autowired
    private UserRepository repo;
    private PasswordEncoder passwordEncoder;


    public ServiceUserImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User addUser(User u) {

      String encodedPassword = passwordEncoder.encode(u.getPassword());
        u.setPassword(encodedPassword);
       Optional<User> existingUser =  repo.findByEmail(u.getEmail());
        if (existingUser.isPresent()) {
            // User with this email already exists, so don't save
            return null;
        }
            return repo.save(u);

    }

    @Override
    public String updateUser(Integer id, User u) {

         Optional<User>u1= repo.findById(id);
if(u1.isPresent())
{
    User existingUser= u1.get();

    existingUser.setName(u.getName());
    existingUser.setPassword(u.getPassword());
    existingUser.setRole(u.getRole());

        repo.save(existingUser);
        return "Upated Successfully";
}


        return "User not found";
    }

    @Override
    public      Optional<User>         getOneUser(Integer id) {
        return repo.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public void deleteUser(Integer id) {
     repo.deleteById(id);
    }
}
