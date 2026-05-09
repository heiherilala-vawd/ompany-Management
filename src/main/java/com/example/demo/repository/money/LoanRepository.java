package com.example.demo.repository.money;

import com.example.demo.model.money.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository
    extends JpaRepository<Loan, String>, JpaSpecificationExecutor<Loan> {}
