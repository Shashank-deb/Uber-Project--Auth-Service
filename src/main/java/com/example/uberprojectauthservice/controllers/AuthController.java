package com.example.uberprojectauthservice.controllers;


import com.example.uberprojectauthservice.dto.AuthRequestDTO;
import com.example.uberprojectauthservice.dto.AuthResponseDTO;
import com.example.uberprojectauthservice.dto.PassengerDTO;
import com.example.uberprojectauthservice.dto.PassengerSignUpRequestDTO;
import com.example.uberprojectauthservice.services.AuthService;
import com.example.uberprojectauthservice.services.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup/passenger")
    public ResponseEntity<PassengerDTO> signUp(@RequestBody PassengerSignUpRequestDTO passengerSignUpRequestDTO) {
        PassengerDTO response = authService.signupPassenger(passengerSignUpRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDTO authRequestDTO,
                                    HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDTO.getEmail().trim(),
                            authRequestDTO.getPassword().trim()
                    )
            );

            String token = jwtService.createToken(authRequestDTO.getEmail());

            ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", token)
                    .httpOnly(true)
                    .secure(false)          // true in production (HTTPS)
                    .path("/")
                    .maxAge(60 * 60)        // seconds (1 hour)
                    .sameSite("Strict")     // Lax / None (for cross-site)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(AuthResponseDTO.builder().success(true).build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out");
    }


}
