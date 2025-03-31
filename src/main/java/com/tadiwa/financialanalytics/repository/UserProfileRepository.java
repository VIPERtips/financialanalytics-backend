package com.tadiwa.financialanalytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

	UserProfile findByUser(User user);

	

}
