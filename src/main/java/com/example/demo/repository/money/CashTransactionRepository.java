package com.example.demo.repository.money;

import com.example.demo.model.money.CashTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CashTransactionRepository
    extends JpaRepository<CashTransaction, String>, JpaSpecificationExecutor<CashTransaction> {}
