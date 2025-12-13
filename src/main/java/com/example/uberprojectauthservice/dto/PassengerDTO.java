package com.example.uberprojectauthservice.dto;

import com.example.uberprojectauthservice.models.Passenger;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {

    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Date createdAt;


    public static PassengerDTO from(Passenger p){
        return PassengerDTO.builder()
                .id(String.valueOf(p.getId()))
                .name(p.getName())
                .email(p.getEmail())
                .password(p.getPassword())
                .phoneNumber(p.getPhoneNumber())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
