package com.example.testBA.dto;


import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;

}
