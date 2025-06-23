package com.example.jwtsecurity.util;


//to generate the JWT Token

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    //1.create the token
    //the method in which we are using the claims up to us and set the subject usually
    // HEADER.PAYLOAD.SIGNATURE
//header is the secret key the payload is the mail and the issued and the expiring dates and the signature is the
//Header	Algorithm used for signature (e.g., HS256), type JWT
//Payload	Subject (email/username), issued date, expiry, claims
//Signature	Signed hash using secret key and header+payload

    private String createToken(     Map<String, Object> claims, String subject) {

        //private String createToken( String subject) {
        return Jwts.builder()

                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Current time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Valid for 10 hours
                .signWith(SignatureAlgorithm.HS256, secret) // Sign with secret key and algorithm
                .compact(); // Build the token
    }


    //2.generate the token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,username);
    }

    //3.check the token whether it is right or not

    public String   extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        //the method reference where claims is the class and the getSubject is the method
        //the subject is the mail
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Get all claims
        return claimsResolver.apply(claims); // Apply function to claims
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 7. Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 8. Decode and get all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret) // Use the same secret used to sign
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get the payload (claims)
    }



}