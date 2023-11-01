package com.partyCheck.Backend.auth;

import com.partyCheck.Backend.auth.AuthResponse;
import com.partyCheck.Backend.auth.LoginRequest;
import com.partyCheck.Backend.auth.RegisterRequest;
import com.partyCheck.Backend.entity.Role;
import com.partyCheck.Backend.entity.User;
import com.partyCheck.Backend.repository.IUserRepository;
import com.partyCheck.Backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login (LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            if (e instanceof UsernameNotFoundException) {
                throw new UsernameNotFoundException("Email not found");
            } else {
                throw new BadCredentialsException("Password invalid");
            }
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        Role userRole = user.getRole();
        String userId = user.getUserId().toString();

        return AuthResponse.builder()
                .token(token)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .role(userRole.toString())
                .userId(userId)
                .build();

    }

    public AuthResponse register (RegisterRequest request){
        if (request.getPassword().length() < 5){
            throw new IllegalArgumentException("Password should be at least 5 characters.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        Role userRole = user.getRole();
        Integer userId = user.getUserId();

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(userRole.toString())
                .userId(userId.toString())
                .build();
    }

    public void toggleAdminAccess (String email) {
        Optional <User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getRole() == Role.ROLE_ADMIN){
                user.setRole(Role.ROLE_USER);
            }else {
                user.setRole(Role.ROLE_ADMIN);
            }
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found with email: " + email );
            }
        }


    }
