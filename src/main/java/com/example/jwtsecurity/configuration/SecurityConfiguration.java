package com.example.jwtsecurity.configuration;

import com.example.jwtsecurity.filter.JwtAutheticationFilter;
import com.example.jwtsecurity.filter.EnhancedJwtAuthenticationFilter;
import com.example.jwtsecurity.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAutheticationFilter jwtAuthFilter;
    private final EnhancedJwtAuthenticationFilter enhancedJwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
// | Line                          | What it means                                 |
// | ----------------------------- | --------------------------------------------- |
// | `@Bean`                       | Make this method available to Spring Boot     |
// | `SecurityFilterChain`         | Define how Spring Security protects your app  |
// | `securityMatcher(...)`        | Apply these rules only to some routes         |
// | `csrf().disable()`            | Disable CSRF because you're using JWT         |
// | `sessionManagement(...)`      | Use stateless tokens (no sessions)            |
// | `authorizeHttpRequests(...)`  | Define which APIs are public or protected     |
// | `authenticationProvider(...)` | Use your custom login logic                   |
// | `addFilterBefore(...)`        | Check token in the request before login logic |
// | `http.build()`                | Finish and return the security setup          |

    @Bean
    @Order(1)
    public SecurityFilterChain acessTokenFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/**", "/users/user")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/user").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain giveAuthorityFilterChain(HttpSecurity http) throws Exception {
        http
                // Fixed: Use ** instead of *** for wildcard matching
                .securityMatcher("/users/user/update", "/users/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/user/update").hasAnyRole("ADMIN", "USER")
                        // Fixed: Use ** instead of *** for wildcard matching
                        .requestMatchers("/users/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(enhancedJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
//    // 2. Inject your UserDetailsService implementation
//only for the 1st time when the user isbeing logged in with the email and the password
    //| Step                  | Component                                              | Used? | When                                   |
// | --------------------- | ------------------------------------------------------ | ----- | -------------------------------------- |
// | Login request         | `AuthenticationProvider` (via `AuthenticationManager`) |  Yes | When logging in                        |
// | JWT-protected request | `JwtFilter`                                            |  Yes | Every secured request                  |
// | JWT-protected request | `AuthenticationProvider`                               |  No  | Skipped because token already verified |

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

   // 1. Password encoder (used in registration & authentication)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    public static String getPermissionAsString() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getAuthorities() == null) {
            return "";
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
