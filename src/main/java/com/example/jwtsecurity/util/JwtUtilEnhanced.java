package com.example.jwtsecurity.util;

import com.example.jwtsecurity.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.jws.soap.SOAPBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtilEnhanced {

    @Autowired
    private JwtUtil util;

    @Value("${jwt.enhanced.secret}")
   private String secret ;


//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//    }


//            SecretKeyGenerator.generateSecureToken(32);
//    private Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    //Cryptographic key derived from the secret to sign JWTs






    //1.create the token
    //the method in which we are using the claims up to us and set the subject usually
    // HEADER.PAYLOAD.SIGNATURE
//header is the secret key the payload is the mail and the issued and the expiring dates and the signature is the
//Header	Algorithm used for signature (e.g., HS256), type JWT
//Payload	Subject (email/username), issued date, expiry, claims
//Signature	Signed hash using secret key and header+payload
    public String createToken(String token ,Integer id, String name, String role) {

        //extract the userheader or email from the token and passing that as the argument here
                 String email = util.extractUsername(token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("name", name);
        claims.put("role", role);
       claims.put("email", email);



        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith( SignatureAlgorithm.HS256,secret)
                .compact();

    }


    public String generateTokenFromCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String token = null ;
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Integer id = userDetails.getId();
        String name = userDetails.getName();
        String role = userDetails.getRole();
        //String email = userDetails.getEmail();



        return createToken( token ,id, name, role);
    }
    //3.check the token whether it is right or not

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        //the method reference where claims is the class and the getSubject is the method
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Get all claims
        return claimsResolver.apply(claims); // Apply function to claims
    }

    public boolean validateToken(String token, String username, String expectedRole) {
        final String extractedUsername = extractUsername(token);
        final String extractedRole = extractAllClaims(token).get("role", String.class);

        return extractedUsername.equals(username)
                && extractedRole.equals(expectedRole)
                && !isTokenExpired(token);
    }


    // 7. Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 8. Decode and get all claims from token
    public  Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret) // Use the same secret used to sign
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get the payload (claims)
    }


}
