package com.example.uberprojectauthservice.services;

import com.example.uberprojectauthservice.helpers.AuthPassengerDetails;
import com.example.uberprojectauthservice.models.Passenger;
import com.example.uberprojectauthservice.repositories.PassengerRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private PassengerRepository passengerRepository;

    public UserDetailsServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Passenger> passengerByEmail = passengerRepository.findPassengerByEmail(email);
        if (passengerByEmail.isPresent()) {
            return new AuthPassengerDetails(passengerByEmail.get());
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
