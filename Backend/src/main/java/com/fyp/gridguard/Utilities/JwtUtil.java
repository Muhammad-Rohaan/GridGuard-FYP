package com.fyp.gridguard.Utilities;

import com.fyp.gridguard.Models.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;


    public String generateJwtToken(Users user) {

        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("role", user.getRoles().name());

        String jwtToken = Jwts.builder()
                .setClaims(userClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();

        return jwtToken;
    }

    public boolean validateJwtToken(String jwtToken, UserDetails userDetails) {

        boolean flag = false;
        String username = getUserEmailFromJwtToken(jwtToken);

        if (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken)) {
            flag = true;
        }
//        System.out.println(username);  /// email
//        System.out.println("ValidateJWTtoken -> userDetails.getUsername() "+userDetails.getUsername());  ///Raffay
//        System.out.println("ValidateJWTtoken -> token exp = "+isTokenExpired(jwtToken));
//        System.out.println("Validated user = "+flag);
        return flag;



//        return (username.equals(userDetails.getUsername()) && userDetails.isAccountNonExpired());
    }

    private boolean isTokenExpired(String jwtToken) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());
    }

    public String getRolesFromJwtToken(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .get("role", String.class);
    }

    public String getUserEmailFromJwtToken(String jwtToken) {
//        System.out.println("getUserEmailFromJwtToken → "+ jwtToken);
        String userEmail = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();

        return userEmail;
    }



}

