package com.example.uberprojectauthservice.controllers;

import com.example.uberprojectauthservice.helpers.AuthPassengerDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Backend is running";
    }


    @GetMapping("/api/v1/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {

        AuthPassengerDetails user =
                (AuthPassengerDetails) auth.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getUsername());
        response.put("phone", user.getPhoneNumber());
        response.put("id", user.getId());

        return ResponseEntity.ok(response);
    }


}
