package com.example.testBA.repository;

import com.example.testBA.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
