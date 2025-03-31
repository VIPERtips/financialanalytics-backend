package com.tadiwa.financialanalytics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tadiwa.financialanalytics.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	List<User> findByRole(String role);

	User findByEmail(String email);

	User findByResetToken(String token);

	boolean existsByEmail(String email);

}
