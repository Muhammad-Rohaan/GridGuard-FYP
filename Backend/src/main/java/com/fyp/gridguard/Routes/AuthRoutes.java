package com.fyp.gridguard.Routes;


import com.fyp.gridguard.Models.Users;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;


public abstract class AuthRoutes {

    public abstract ResponseEntity<String> registerUser(Users user);
    public abstract ResponseEntity<String> login(Users loginUser, HttpServletResponse httpServletResponse);
}
