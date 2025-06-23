package com.example.jwtsecurity.filter;


import com.example.jwtsecurity.configuration.SecurityConfiguration;
import com.example.jwtsecurity.security.CustomUserDetailsService;
import com.example.jwtsecurity.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public  class JwtAutheticationFilter extends OncePerRequestFilter {

    //OncePerRequestFilter executes the filter only once even if that is passed multiple times and is a abstract class with the
    //methods of the doFilterInternal

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
     private CustomUserDetailsService userDetailsService;


    //The Authorization: Bearer <token> header format is a standard convention used in APIs,
    // especially when working with token-based authentication like JWT.
    //The Authorization header is a standard HTTP header defined by the HTTP specification.
    //Bearer is a type of token used in OAuth 2.0 and JWT-based systems. it's like saying here is the token please check
    //it and give the access


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //the request that comes in that has to start with the Authorization
                          final String authHeader = request.getHeader("Authorization");
                          final String token;
                          final String header;
       // String path = request.getServletPath();

        if(authHeader==null||!authHeader.startsWith("Bearer "))
                          {
                           filterChain.doFilter(request,response);
                           return;
                           //does nothing just is a way of saying that the required stuffs like the Authorization
                              //or the Bearer is not there so I am not going to anything special with this I am going to pass
                              //this to the next filter
                              //If the token is missing or invalid, you may decide not to do anything special
                              // and just let the request continue normally
                          }
           token= authHeader.substring(7);
        //bearer along with the space makes it the 6 characters and starting from the token so the 7th position
        System.out.println("JWT  TOKEN EXTRACED" + token);

        try {
            header = jwtUtil.extractUsername(token);
        }catch (io.jsonwebtoken.ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"JWT token is expired\"}");
            response.getWriter().flush();
            response.getWriter().close();
            return;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
        //When you're in a Filter, you are outside the controller
        // lifecycle — so you must manually write to the response if you want to return JSON.
        //| `@RestController` methods                 | Controller layer
        // | `throw new CustomException()` (handled by `@ExceptionHandler`) |
        //| `Filter` (like `JwtAuthenticationFilter`) | Filter chain (security level)
        // | Manually write to `HttpServletResponse`                        |

        System.out.println("This is extracted Header(email)  "  +header);



if(header!=null  && SecurityContextHolder.getContext().getAuthentication()==null)
//SecurityContextHolder.getContext().getAuthentication()==null---
    // Spring Security stores details about the currently authenticated user in something called the
    // SecurityContext which is a custom utility class.
    //getContext() returns the current security context.
    //getAuthentication () Authentication object — which holds information  like username email and the role along
    // with the user is  authenticated
{
    //after we send the data from the CustomUserDetailsSerivce it gets stored inside this UserDetailsObject
    // userDetailsService.loadUserByUsername(email) returns UserDetails object
//we are going to write the implementation for this and even the Class is the CustomUSerDeatils which I have created
    //but what I we get the return from that is this the UserDetails object and the data is stored inside these specified methods
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(header);

    String emailFromDatabase = userDetails.getUsername();
    //As I have mentioned above the same thing the data is stored inside these methods of the UserDetails abstarct class
    // UserDetails interface has getUsername() and teh getemail because
    //this is where all the data gets stored which we send from the


System.out.println("this is the email from the database the header which is being extracted  " + emailFromDatabase);

    if(jwtUtil.validateToken(token,emailFromDatabase))
    {
//This is a standard Spring Security class used to represent an authenticated user.
        //creating the authenticated password
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        //its like saying to the Spring that the user I found and his jwt token is verified and these are his roles
        //This object holds info like username, roles, and whether they're authenticated  manily  its the object which stores the information
        //Since it’s created by you, Spring assumes it’s already authenticated.
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//this is like we are going to remeber the IP adress, session information and also the stuffs of the http requests

        //Spring context holder holds all the information inside it about the person he is authenticated or not like that
// “This user is now authenticated — treat this request as coming from a logged-in user.”s
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("hi");

    }
    else {

            System.out.println("JWT is invalid or expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorJson = "{\"error\": \"JWT token is expired or invalid\"}";
            response.getWriter().write(errorJson);
            response.getWriter().flush(); // flush buffer
            response.getWriter().close(); // close writer

            return; // IMPORTANT: stop further processing
        }


    }



        filterChain.doFilter(request, response);
//it's saying I am done doing my job now pass this to the next one or to the next filter

    }


}
