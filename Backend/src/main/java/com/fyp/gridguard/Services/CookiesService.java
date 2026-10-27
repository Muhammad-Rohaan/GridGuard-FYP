package com.fyp.gridguard.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookiesService {

    @Value("${gridguard.auth.cookie-secure:false}")
    private boolean secure;

    public ResponseCookie createJwtCookie(String jwtToken) {
        return ResponseCookie.from("jwt_token", jwtToken)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // for 7 days
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie deleteJwtCookie() {
        return ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

}
