package com.tadiwa.financialanalytics.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserDto;
import com.tadiwa.financialanalytics.model.UserProfile;
import com.tadiwa.financialanalytics.repository.UserProfileRepository;
import com.tadiwa.financialanalytics.repository.UserRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
   @Autowired
   private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void generateResetToken(User user) {
        String token = UUID.randomUUID().toString(); 
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(30)); 
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); 
        user.setTokenExpiration(null);
        userRepository.save(user);
    }
    
    public List<UserDto> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(user -> new UserDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail()
                        
                ))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return new UserDto(
                user.getUserId(),user.getUsername(),
                user.getEmail()        );
    }

    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username " + username));
    }
    
    public User getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return user;
    }
    
    public UserProfile getUserProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user);
                if(profile == null) {
                	throw new RuntimeException("Profile not found for user ID: " + user.getUserId());
                }
				return profile;
    }
}
