package com.example.jwtsecurity.filter;

import com.example.jwtsecurity.security.CustomUserDetailsService;
import com.example.jwtsecurity.util.JwtUtilEnhanced;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.io.IOException;

//filter to check the new enhanced filter
@Service
public class EnhancedJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilEnhanced jwt;

    @Autowired
    private CustomUserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

     final String authHeader = request.getHeader("X-Access-Token");
     final String token;


     if(authHeader==null||!authHeader.startsWith("Bearer"))
     {
         filterChain.doFilter(request,response);

         return ;
     }

     token= authHeader.substring(7);
     System.out.println(token);
     try {
         Claims claims = jwt.extractAllClaims(token);
         String header = jwt.extractUsername(token);
         //The Claims.get(String key, Class<T> requiredType) method is a generic method provided by the JWT library (io.jsonwebtoken.Claims).
         // It allows you to get a value from the token payload and cast it to a specific type.
         //if its the String then String.class,Integer-Integer.class,Long-Long.class
         final String role = claims.get("role", String.class);
         System.out.println("The extraced Role:  " + role);


         UserDetails userDetails = this.userDetailsService.loadUserByUsername(header);
         String databaseemail = userDetails.getUsername();
         Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

         String databaseRole = authorities.stream()
                 .map(GrantedAuthority::getAuthority)
                 .map(role1 -> role1.replace("ROLE_", ""))      // removes "ROLE_" prefix
                 .findFirst()
                 .orElse(null);

         if (jwt.validateToken(token, databaseemail, databaseRole)) {
             UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                     userDetails, null, userDetails.getAuthorities());
             authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
             SecurityContextHolder.getContext().setAuthentication(authentication);
             System.out.println("Authentication successful");
         }

     else {
            sendErrorResponse(response, "JWT token is invalid or does not match role/email");
            return;
        }
    } catch (io.jsonwebtoken.ExpiredJwtException ex) {
        sendErrorResponse(response, "JWT token is expired");
        return;
    } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
        sendErrorResponse(response, "Invalid JWT token");
        return;
    } catch (Exception ex) {
        sendErrorResponse(response, "Authentication error: " + ex.getMessage());
        return;
    }


        filterChain.doFilter(request, response);
        }


        private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"" + message + "\"}");
            response.getWriter().flush();
            response.getWriter().close();
        }


    }

