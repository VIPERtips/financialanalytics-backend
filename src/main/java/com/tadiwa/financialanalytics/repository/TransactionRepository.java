package com.tadiwa.financialanalytics.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tadiwa.financialanalytics.model.Transaction;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.service.TransactionService;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

   // @Query("SELECT t FROM Transaction t WHERE t.user = :user")
    List<Transaction> findByUser(@Param("user") User user);
    
    Page<Transaction> findByUser(User user, Pageable pageable);

	List<Transaction> findByDateBetween(LocalDate from, LocalDate to);

	void deleteAllByUser(User user);

	List<Transaction> findByUser_UserIdAndDateBetween(int userId, LocalDate from, LocalDate to);

	

}

