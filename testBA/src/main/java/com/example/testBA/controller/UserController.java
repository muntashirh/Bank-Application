package com.example.testBA.controller;

import com.example.testBA.dto.*;
import com.example.testBA.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(
            summary = "Creating a new account",
            description = "Creating a new user and assigning it to an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP STATUS 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Displaying balance information on the given account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP STATUS 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @Operation(
            summary = "Name Enquiry",
            description = "Displaying Name and Account Number for given account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP STATUS 200 SUCCESS"
    )
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.nameEnquiry(enquiryRequest);
    }


    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponse transferAccount(@RequestBody TransferRequest request){
        return userService.transferAccount(request);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
}
