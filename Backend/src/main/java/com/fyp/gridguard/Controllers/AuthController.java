package com.fyp.gridguard.Controllers;


import com.fyp.gridguard.Models.Users;
import com.fyp.gridguard.Routes.AuthRoutes;
import com.fyp.gridguard.Services.CookiesService;
import com.fyp.gridguard.Services.UserService;
import com.fyp.gridguard.Utilities.JwtUtil;
import com.fyp.gridguard.Utilities.Roles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends AuthRoutes {

    @Autowired
    private UserService userService;

    @Autowired
    private CookiesService cookiesService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {

        try {
            if (user.getRoles() == null) {
                user.setRoles(Roles.USER);
            }
            userService.userRegistration(user);

            return ResponseEntity.status(201).body("User Registered Successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("# Error in registerUser() #   " + e.getMessage());
        }

    }


    // Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users loginUser, HttpServletResponse httpServletResponse) {
        try {
            Users curUser = userService.findByEmail(loginUser.getEmail());

            if (curUser != null && BCrypt.checkpw(loginUser.getPassword(), curUser.getPassword())) {
                String jwtToken = jwtUtil.generateJwtToken(curUser);   /// 14/2/25 | cookies

                ResponseCookie cookie = cookiesService.createJwtCookie(jwtToken);
                httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                return ResponseEntity.status(200).body("Login Successful");
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");

            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("# Error in Login() #   " + e.getMessage());
        }

    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "id", user.getUserId(),
                "name", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRoles().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookiesService.deleteJwtCookie().toString());
        return ResponseEntity.noContent().build();
    }


}
