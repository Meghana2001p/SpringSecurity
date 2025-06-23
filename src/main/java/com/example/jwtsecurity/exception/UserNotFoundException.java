package com.example.jwtsecurity.exception;

//if the user wants to log in and he is not present then at that time this exception is thrown
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message)
    {
        super(message);
    }


}
