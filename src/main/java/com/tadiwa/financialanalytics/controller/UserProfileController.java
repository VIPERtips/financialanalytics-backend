
package com.tadiwa.financialanalytics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tadiwa.financialanalytics.model.ApiResponse;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserProfile;
import com.tadiwa.financialanalytics.service.JwtService;
import com.tadiwa.financialanalytics.service.UserProfileService;
import com.tadiwa.financialanalytics.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserProfile>> createOrUpdateUserProfile(HttpServletRequest request,
                                                                              @RequestBody UserProfile userProfile) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (userProfile.getIncome() <= 0) {
                throw new RuntimeException("Income must be a positive number.");
            }
            UserProfile profile = userProfileService.createOrUpdateUserProfile(user.getUserId(), userProfile);
            return ResponseEntity.ok(new ApiResponse<>("User profile updated successfully", true, profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
    
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserProfile>> updateUserProfile(HttpServletRequest request,
                                                                      @RequestBody UserProfile userProfile) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (userProfile.getIncome() <= 0) {
                throw new RuntimeException("Income must be a positive number.");
            }
            UserProfile profile = userProfileService.createOrUpdateUserProfile(user.getUserId(), userProfile);
            return ResponseEntity.ok(new ApiResponse<>("User profile updated successfully", true, profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserProfile>> getUserProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            UserProfile profile = userProfileService.getUserProfileByUserId(user.getUserId());
            return ResponseEntity.ok(new ApiResponse<>("User profile retrieved successfully", true, profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
    
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> deleteUserProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            userProfileService.deleteUserProfile(user.getUserId());
            return ResponseEntity.ok(new ApiResponse<>("User profile deleted successfully", true, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
