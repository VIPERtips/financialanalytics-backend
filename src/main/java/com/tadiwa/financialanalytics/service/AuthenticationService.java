package com.tadiwa.financialanalytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tadiwa.financialanalytics.model.AuthenticationResponse;
import com.tadiwa.financialanalytics.model.LoginDto;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserDto;
import com.tadiwa.financialanalytics.repository.UserRepository;



@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

   @Autowired
   private EmailSender emailSender;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthenticationResponse registerUser(UserDto req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        if (req.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Oops, username taken. Enter a unique username.");
        }
        
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Oops, email taken. Enter a unique email.");
        }

        
       

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());

        userRepository.save(user);
       

        emailSender.sendRegistrationEmail(req.getEmail(), req.getUsername(), req.getPassword(), req.getEmail());
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, user.getRole(), refreshToken);
    }

    public AuthenticationResponse authenticate(LoginDto req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, user.getRole(), refreshToken);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        return new AuthenticationResponse(newAccessToken, user.getRole(), refreshToken);
    }
}
