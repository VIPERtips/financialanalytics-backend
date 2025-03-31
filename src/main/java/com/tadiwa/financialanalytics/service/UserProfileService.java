package com.tadiwa.financialanalytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserProfile;
import com.tadiwa.financialanalytics.repository.TransactionRepository;
import com.tadiwa.financialanalytics.repository.UserProfileRepository;
import com.tadiwa.financialanalytics.repository.UserRepository;

@Service
public class UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService; 
    
    
    @Autowired
    private TransactionRepository transactionRepository;
    @Transactional
    public UserProfile createOrUpdateUserProfile(int userId, UserProfile userProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));
        
        // Check if UserProfile exists
        UserProfile existingProfile = userProfileRepository.findByUser(user);
        if (existingProfile == null) {
            userProfile.setUser(user);
            return userProfileRepository.save(userProfile);
        } else {
            // If profile exists, update it
            existingProfile.setName(userProfile.getName());
            existingProfile.setCurrency(userProfile.getCurrency());
            existingProfile.setIncome(userProfile.getIncome());
            existingProfile.setTheme(userProfile.getTheme());
            return userProfileRepository.save(existingProfile);
        }
    }
    
    
    public UserProfile getUserProfileByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));
        return userProfileRepository.findByUser(user);
    }

    public void deleteUserProfile(int userId) {
       
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));
        
        
        transactionRepository.deleteAllByUser(user);

        
        UserProfile userProfile = userProfileRepository.findByUser(user);
        if (userProfile != null) {
            userProfileRepository.delete(userProfile);
        }

       
        userRepository.delete(user);
    }


    
    public boolean validateProfile(UserProfile profile) {
        
        if (profile.getIncome() <= 0) {
            throw new RuntimeException("Income must be a positive number.");
        }
        return true;
    }
}
