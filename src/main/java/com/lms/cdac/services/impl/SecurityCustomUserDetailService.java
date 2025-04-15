package com.lms.cdac.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lms.cdac.repsitories.UserRepositories;

@Service
public class SecurityCustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepositories userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user by email
        return userRepo.findByEmail(username)  // Use the correct userRepo instance
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));
    }
}
 