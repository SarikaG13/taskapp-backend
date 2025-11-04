package com.example.TaskApp.security;

import com.example.TaskApp.entity.User;
import com.example.TaskApp.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(AuthUser::new)
                .orElseThrow(() -> {
                    System.out.println(" User not found during authentication: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }
}