package com.tadiwa.financialanalytics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tadiwa.financialanalytics.model.Budget;
import com.tadiwa.financialanalytics.model.User;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

	Page<Budget> findByUser(User user, Pageable pageable);

}
