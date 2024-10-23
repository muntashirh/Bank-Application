package com.example.testBA.service.impl;

import com.example.testBA.dto.EmailDto;
import com.example.testBA.entity.Transaction;
import com.example.testBA.entity.User;
import com.example.testBA.repository.TransactionRepository;
import com.example.testBA.repository.UserRepository;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class BankStatement {

    //get list of transactions within a set date and create a pdf file
    //send pdf via email
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE = "C:\\Users\\munta\\OneDrive\\MyStatement.pdf";


    public List <Transaction> generateStatements(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate,DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);
        List <Transaction> transactionsList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start))
                .filter(transaction -> transaction.getCreatedAt().isEqual(end))
                .toList();
        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName();
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of documents");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Bank Application"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.GREEN);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Account Address"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date" + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date" + endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name:" + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Address:" + user.getAddress()));
        address.setBorder(0);

        //transactionTable
        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.GREEN);
        date.setBorder(0);
        PdfPCell transactionsType = new PdfPCell(new Phrase("Transaction Type:"));
        transactionsType.setBackgroundColor(BaseColor.GREEN);
        transactionsType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.GREEN);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.GREEN);
        status.setBorder(0);


        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionsType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionsList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(status.toString()));

        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);


        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();
        EmailDto emailDto = EmailDto.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Here is your requested account statement attached:")
                .attachment(FILE)
                .build();
        emailService.sendEmailwithAttachments(emailDto);

        return transactionsList;
    }

}
