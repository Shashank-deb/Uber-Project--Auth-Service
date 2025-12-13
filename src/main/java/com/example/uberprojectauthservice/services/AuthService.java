
package com.example.uberprojectauthservice.services;
import com.example.uberprojectauthservice.dto.PassengerDTO;
import com.example.uberprojectauthservice.dto.PassengerSignUpRequestDTO;
import com.example.uberprojectauthservice.models.Passenger;
import com.example.uberprojectauthservice.repositories.PassengerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(PassengerRepository passengerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passengerRepository = passengerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public PassengerDTO signupPassenger(PassengerSignUpRequestDTO passengerSignUpRequestDTO) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignUpRequestDTO.getEmail())
                .name(passengerSignUpRequestDTO.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignUpRequestDTO.getPassword()))
                .phoneNumber(passengerSignUpRequestDTO.getPhoneNumber())
                .build();
        Passenger newPassenger=passengerRepository.save(passenger);

        PassengerDTO response=PassengerDTO.from(newPassenger);

        return response;
    }


}
