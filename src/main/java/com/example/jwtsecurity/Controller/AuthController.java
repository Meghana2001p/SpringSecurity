package com.example.jwtsecurity.Controller;


import com.example.jwtsecurity.Entity.AuthRequest;
import com.example.jwtsecurity.Entity.AuthResponse;
import com.example.jwtsecurity.security.CustomUserDetailsService;
import com.example.jwtsecurity.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    //it is used to manage the authentication process like validating the username/email or password
    @Autowired
    private CustomUserDetailsService userDetailsService;
    //to load the data from the database
    @Autowired
    private JwtUtil jwtUtil;
//to create the token for the jwt

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {

            //authenticate or get the data using the Authentication manager


            //In Spring Security, the AuthenticationManager is the main engine that
            // handles user authentication.(checking whether the user exists or not)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    ));
//Wraps the login data into a token object (UsernamePasswordAuthenticationToken)
//Sends it to AuthenticationManager
//If it's successful, authentication is returned
//If not, it throws an exception (BadCredentialsException, etc.)
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            System.out.println(" The generated token is  " + token);
            return ResponseEntity.ok(new AuthResponse(token));

        }

    }






