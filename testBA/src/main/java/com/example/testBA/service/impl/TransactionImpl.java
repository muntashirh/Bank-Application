package com.example.testBA.service.impl;

import com.example.testBA.dto.TransactionDto;
import com.example.testBA.entity.Transaction;
import com.example.testBA.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
    Transaction transaction = Transaction.builder()
            .transactionType(transactionDto.getTransactionType())
            .accountNumber(transactionDto.getAccountNumber())
            .amount(transactionDto.getAmount())
            .status("SUCCESS")
            .build();
    transactionRepository.save(transaction);
    System.out.println("Transaction saved successfully");
    }

}
