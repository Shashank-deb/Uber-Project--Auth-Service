package com.example.uberprojectauthservice.helpers;

import com.example.uberprojectentityservice.models.Passenger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AuthPassengerDetails extends Passenger implements UserDetails {

    public AuthPassengerDetails(Passenger passenger) {
        this.setId(passenger.getId());
        this.setEmail(passenger.getEmail());
        this.setPassword(passenger.getPassword());
        this.setPhoneNumber(passenger.getPhoneNumber());
        this.setCreatedAt(passenger.getCreatedAt());
        this.setUpdatedAt(passenger.getUpdatedAt());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
