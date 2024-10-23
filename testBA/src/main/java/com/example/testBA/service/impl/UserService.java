package com.example.testBA.service.impl;

import com.example.testBA.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transferAccount(TransferRequest transferRequest);
    BankResponse login(LoginDto loginDto);
}
