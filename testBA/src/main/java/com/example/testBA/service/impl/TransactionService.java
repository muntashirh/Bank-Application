package com.example.testBA.service.impl;

import com.example.testBA.dto.TransactionDto;
import com.example.testBA.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);

}
