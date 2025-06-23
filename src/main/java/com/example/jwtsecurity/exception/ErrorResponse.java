package com.example.jwtsecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


//the class which is responsible for giving the response about what kind of exception this is
//like we can go ahead and say which is and what is the type of the exception we are facing along with the stauts error
//message
@Data
@AllArgsConstructor
@NoArgsConstructor
@RestController
public class ErrorResponse {

private LocalDateTime timestamp;
private int status;
private String error;
private String message;

}
