package com.example.testBA.service.impl;

import com.example.testBA.configure.JwtTokenProvider;
import com.example.testBA.dto.*;
import com.example.testBA.entity.Role;
import com.example.testBA.entity.User;
import com.example.testBA.repository.UserRepository;
import com.example.testBA.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        //Creating Account and saving it
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .address(userRequest.getAddress())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.valueOf(0))
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();
        User savedUser = userRepository.save(user);
        //send email notification
        EmailDto emailDto = EmailDto.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATED")
                .messageBody("Your account has been successfully created\n Account Details:\n " +
                        "   Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() +
                        "\n     Account Number:" + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDto);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );

        EmailDto loginAlert = EmailDto.builder()
                .subject("You have logged in!")
                .recipient(loginDto.getEmail())
                .messageBody("You have officially logged in! Contact your bank account if you have not initiated this request!")
                .build();

        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseMessage("Login Successful")
                .responseCode(jwtTokenProvider.generateToken(authentication))
                .build();
    }



    //Balance Enquiry
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        //check if account exists and return accountInfo
        boolean accountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User givenUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber()) ;
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(givenUser.getAccountBalance())
                        .accountNumber(givenUser.getAccountNumber())
                        .accountName(givenUser.getFirstName() + " " +givenUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExists){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User givenUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return givenUser.getFirstName() + " " + givenUser.getLastName();
    }

    // deposit money to account
    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //check if account exists
        boolean accountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User givenUser = userRepository.findByAccountNumber(request.getAccountNumber());
        givenUser.setAccountBalance(givenUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(givenUser);

        //Save Transfer
        TransactionDto  transactionDto = TransactionDto.builder()
                .accountNumber(givenUser.getAccountNumber())
                .transactionType("CREDIT/DEPOSIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build();

        transactionService.saveTransaction(transactionDto);
        System.out.println("Transaction saved successfully");
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(givenUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(givenUser.getFirstName() + " " + givenUser.getLastName())
                        .build())
                .build();
    }

    //withdraw money from account
    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if account exists
        boolean accountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User givenUser = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger currentBalance = givenUser.getAccountBalance().toBigInteger();
        BigInteger amountWithdraw = request.getAmount().toBigInteger();
        if (currentBalance.intValue() < amountWithdraw.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_AMOUNT_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBIT_MESSAGE_INSUFFICIENT_AMOUNT)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(givenUser.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(givenUser.getFirstName() + " " + givenUser.getFirstName())
                            .build())
                    .build();
        } else {
            givenUser.setAccountBalance(givenUser.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(givenUser);
            //Save Transfer
            TransactionDto  transactionDto = TransactionDto.builder()
                    .accountNumber(givenUser.getAccountNumber())
                    .transactionType("DEBIT/WITHDRAW")
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .build();

            transactionService.saveTransaction(transactionDto);
            System.out.println("Transaction saved successfully");
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBIT_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBIT_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(givenUser.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(givenUser.getFirstName() + " " + givenUser.getLastName())
                            .build())
                    .build();
        }
    }
    //transfer
    @Override
    public BankResponse transferAccount(TransferRequest transferRequest) {
        boolean isSourceAccountNumber = userRepository.existsByAccountNumber(transferRequest.getSourceNumber());
        boolean isDestinationAccountNumber = userRepository.existsByAccountNumber(transferRequest.getDestinationNumber());
        if (!isDestinationAccountNumber) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User givenUser = userRepository.findByAccountNumber(transferRequest.getSourceNumber());
            if(transferRequest.getAmount().compareTo(givenUser.getAccountBalance()) < 0){
                BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_AMOUNT_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_DEBIT_MESSAGE_INSUFFICIENT_AMOUNT)
                        .accountInfo(AccountInfo.builder()
                                .accountBalance(givenUser.getAccountBalance())
                                .accountNumber(givenUser.getAccountNumber())
                                .accountName(givenUser.getFirstName() + " " + givenUser.getFirstName())
                                .build())
                        .build();
            }
            givenUser.setAccountBalance(givenUser.getAccountBalance().subtract(transferRequest.getAmount()));
            userRepository.save(givenUser);
            EmailDto debitDto = EmailDto.builder()
                    .subject("DEBIT ALERT")
                    .recipient(givenUser.getEmail())
                    .messageBody("The sum of " + " " + transferRequest.getAmount() + " has been withdrawed from your account. Your current balance is:" + givenUser.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(debitDto);
            User destinationUser = userRepository.findByAccountNumber(transferRequest.getDestinationNumber());
            destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(transferRequest.getAmount()));
            userRepository.save(destinationUser);
            EmailDto creditDto = EmailDto.builder()
                .subject("CREDIT ALERT")
                    .recipient(givenUser.getEmail())
                .messageBody("The sum of " + transferRequest.getAmount() + " has been sent to your account from" + givenUser.getFirstName() + " " + givenUser.getLastName() + "." + "Your current balance is:" + destinationUser.getAccountBalance())
                .build();
            emailService.sendEmailAlert(creditDto);

        //Save Transfer
        TransactionDto  transactionDto = TransactionDto.builder()
                .accountNumber(destinationUser.getAccountNumber())
                .transactionType("TRANSFER MONEY BETWEEN ACCOUNTS")
                .amount(transferRequest.getAmount())
                .status("SUCCESS")
                .build();

        transactionService.saveTransaction(transactionDto);
        System.out.println("Transaction saved successfully");
            return BankResponse.builder()
                    .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                    .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }
