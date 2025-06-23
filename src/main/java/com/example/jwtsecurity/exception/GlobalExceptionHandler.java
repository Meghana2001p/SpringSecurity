package com.example.jwtsecurity.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.time.LocalDateTime;

@RestController
@ControllerAdvice
//this annotation is used to define the class that is going to handle the exception that is being thrown by all the
//controller in a project the spring sends that here



//handling the error of the token is directly written in the jwtAuthenticationFilter class
public class GlobalExceptionHandler {

    //Its saying if user not found exception happens any where in the app then we have to call this method
    //we are sending a structured httpResponse with the error response as the object
  @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request)
    {
        ErrorResponse error = new ErrorResponse(

                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User Not Found",
                exception.getMessage()
        );


        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }




    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(NoUserFoundException exception,HttpServletRequest request)
    {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "No Users Exists",
                exception.getMessage()


        );
        return  new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExist( UserAlreadyExists exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Invalid User Input",
                exception.getMessage()



        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    // Generic fallback for all other exceptions
    //the generic method that can be used for other errors
//this is by default which is provided by the Spring we have updated these two lines in the Application Properties
    //spring.mvc.throw-exception-if-no-handler-found=true
//spring.web.resources.add-mappings=false
    //so because of both of this by default the wrong urls will get this error
//the invalid url and all the other errors are handled by this
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Invalid URL",
                exception.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
