package com.fyp.gridguard.Config;


import com.fyp.gridguard.Filters.JwtFilter;
import com.fyp.gridguard.Services.SubstationUserDetailsService;
import com.fyp.gridguard.Utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SubstationUserDetailsService substationUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private JwtUtil jwtUtil;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // 1. Create a clear handler for Spring 7 REST APIs
//        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//        // This tells Spring 7 to use standard, raw strings for comparison rather than the complex XOR format
//        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                // 1. Enable CSRF using standard Cookie repository
                .csrf(csrf -> csrf.disable()
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(requestHandler)
                )
                // 2. Add the custom filter to instantly resolve and expose the cookie
//                .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register-user", "/api/auth/logout", "/").permitAll()
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/technical-worker/**").hasRole("TECHNICAL_WORKER")
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // JWT filter
//         http.addFilterBefore(new JwtFilter(jwtUtil, irpUserDetailsService) /*jwtFilter*/ , UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
//        daoAuthenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
//        return daoAuthenticationProvider;
//    }

    /// Problematic ***
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new SubstationUserDetailsService();
//    }


}
