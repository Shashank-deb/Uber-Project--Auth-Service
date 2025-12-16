package com.example.uberprojectauthservice.controllers;


import com.example.uberprojectauthservice.dto.AuthRequestDTO;
import com.example.uberprojectauthservice.dto.AuthResponseDTO;
import com.example.uberprojectauthservice.dto.PassengerDTO;
import com.example.uberprojectauthservice.dto.PassengerSignUpRequestDTO;
import com.example.uberprojectauthservice.helpers.AuthPassengerDetails;
import com.example.uberprojectauthservice.services.AuthService;
import com.example.uberprojectauthservice.services.JWTService;
import com.example.uberprojectauthservice.services.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;


    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
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

    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request){
        String token=null;
        //Extract JWT Token from cookie
        if(request.getCookies()!=null){
          for(Cookie cookie:request.getCookies()){
              if("JWT_TOKEN".equals(cookie.getName())){
                  token = cookie.getValue();
                  break;
              }
          }
        }


        //If no token in cookie, check Authorization header
        if(token==null){
            String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authHeader!=null && authHeader.startsWith("Bearer ")){
                token = authHeader.substring(7);
            }
        }


        //No token found
        if(token==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid",false,"message","Invalid token"));
        }


        try {
            //Extract email from token using jwtService
            String email=jwtService.extractEmail(token);

            //Validate token(checks expiry and signature)
            if(email!=null && jwtService.validateToken(token, email)){
                //Fetch user details from database using UserDetailsService
                //This reuses the same service that Spring Security  uses for authentication

                UserDetails userDetails=userDetailsServiceImpl.loadUserByUsername(email);


                if(userDetails instanceof AuthPassengerDetails){
                    AuthPassengerDetails authPassengerDetails=(AuthPassengerDetails) userDetails;

                    //Return user information
                    Map<String,Object> response=new HashMap<>();
                    response.put("valid",true);
                    response.put("custom_id",authPassengerDetails.getId());
                    response.put("email",authPassengerDetails.getEmail());
                    response.put("phoneNumber",authPassengerDetails.getPhoneNumber());

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid",false,"message","Invalid token"));
        }
        catch (Exception ex){
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid",false,"message","Token validation failed: "+ ex.getMessage()));
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
