package com.example.jwtsecurity.Controller;



import com.example.jwtsecurity.DTO.UserResponseDTO;
import com.example.jwtsecurity.DTO.UserUpdateDTO;
import com.example.jwtsecurity.Entity.User;
import com.example.jwtsecurity.Repository.UserRepository;
import com.example.jwtsecurity.exception.NoUserFoundException;
import com.example.jwtsecurity.exception.UserAlreadyExists;
import com.example.jwtsecurity.exception.UserNotFoundException;
import com.example.jwtsecurity.serviceImpl.ServiceUserImpl;
import com.example.jwtsecurity.util.JwtUtil;
import com.example.jwtsecurity.util.JwtUtilEnhanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private ServiceUserImpl service;

@Autowired
private JwtUtil jwtUtil;
@Autowired
private UserRepository repo;

@Autowired
private JwtUtilEnhanced jwtUtilEnhanced;

    private PasswordEncoder passwordEncoder;
    public UserController(PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/admin/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        Optional<User> existing = repo.findByEmail(user.getEmail());
        if (existing.isPresent())
        {
            throw new UserAlreadyExists("User with email " + user.getEmail() + " already exists");
        }
        User newUser = service.addUser(user);
        return ok(newUser);
    }
    @GetMapping("/user")
public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String authHeader)
{

String token = authHeader.substring(7);
String userName= jwtUtil.extractUsername(token);
System.out.println(  "userName " +userName);

      Optional<User> user=  repo.findByEmail(userName);
                User user_info=   user.get();

                UserResponseDTO sent_user= new UserResponseDTO();
                sent_user.setEmail(user_info.getEmail());
                sent_user.setName(user_info.getName());
                sent_user.setRole(user_info.getRole());
                sent_user.setId(user_info.getId());


 String advance_token =   jwtUtilEnhanced.createToken(token,user_info.getId(),user_info.getName(),user_info.getRole());
 sent_user.setToken(advance_token);

     if(!user.isPresent())
     {

         throw new UserNotFoundException("User with email " + userName + " not found");

     }
    return ResponseEntity.ok(sent_user);
}

@GetMapping("/admin/all")
public ResponseEntity<List<User>> allUsers()
{
    System.out.println("Hi inside the get All Users method");

    List<User> list=  service.getAllUsers();
    if(list.isEmpty()) {
        throw new NoUserFoundException("User not found");
    }
  return  ok(list);

}

    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestHeader ("X-Access-Token") String token, @RequestBody User user)
    {
                  String extracted_token =  token.substring(7);
                  String header= jwtUtilEnhanced.extractUsername(extracted_token);
            Optional  <User> existingUser= repo.findByEmail(header);
           if(existingUser.isPresent())
           {
             User userExtracted=  existingUser.get();
               UserUpdateDTO updatedUser = new UserUpdateDTO();
               if (!userExtracted.getEmail().equals(user.getEmail())) {
                   Optional<User> emailCheck = repo.findByEmail(user.getEmail());
                   if (emailCheck.isPresent()) {
                       throw new UserAlreadyExists("Email already in use: " + user.getEmail());
                   }
               }
               updatedUser.setEmail(user.getEmail());
               String encodedPassword = passwordEncoder.encode(user.getPassword());
               System.out.println(encodedPassword);
               updatedUser.setPassword(encodedPassword);
               updatedUser.setName(user.getName());
              // updated_user.setRole(user.getRole());

               return ResponseEntity.ok(updatedUser);
           }



           throw new UserNotFoundException("User with ID " + header+ " not found");

    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        Optional<User> existingUserOpt = service.getOneUser(id);

        if (existingUserOpt.isPresent()) {

            service.deleteUser(id);
            return ok("Deleted Successfully");  // Return 204 No Content to signify successful deletion
        }

        // If user not found, return 404 Not Found
        throw new UserNotFoundException("User with ID " + id + " not found");
    }






}
