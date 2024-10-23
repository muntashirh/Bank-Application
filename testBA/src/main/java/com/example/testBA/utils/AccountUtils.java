package com.example.testBA.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created";
    public static final String ACCOUNT_CREATED_MESSAGE = "Account Created Successfully";
    public static final String ACCOUNT_CREATED_CODE = "002";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "This user with the given account does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User found";
    public static final String ACCOUNT_CREDIT_CODE = "005";
    public static final String ACCOUNT_CREDIT_MESSAGE = "User Account Balance Updated";
    public static final String ACCOUNT_DEBIT_CODE = "006";
    public static final String ACCOUNT_INSUFFICIENT_AMOUNT_CODE = "007";
    public static final String ACCOUNT_DEBIT_MESSAGE_INSUFFICIENT_AMOUNT = "Insufficient Amount in your Account";
    public static final String ACCOUNT_DEBIT_MESSAGE = "User Account Balance Updated";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer has been successful";

    public static String generateAccountNumber(){
        Year current = Year.now();
        int min = 100000;
        int max = 999999;
        int randomNum = (int) Math.floor(Math.random() * (max-min +1));
        String year = String.valueOf(current);
        String randomNumber = String.valueOf(randomNum);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
