package com.yourcompany.streamapp.user_service.service;
//import org.springframework.security.core.userdetails.User; // Use an alias to avoid confusion
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.yourcompany.streamapp.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.yourcompany.streamapp.user_service.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is locked.");
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}