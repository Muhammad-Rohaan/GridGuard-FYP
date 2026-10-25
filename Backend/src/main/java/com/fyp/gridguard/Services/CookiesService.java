package com.fyp.gridguard.Services;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookiesService {

    public ResponseCookie createJwtCookie(String jwtToken) {
        return ResponseCookie.from("jwt_token", jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // for 7 days
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie deleteJwtCookie() {
        return ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

}
