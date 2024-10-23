package com.example.testBA.service.impl;

import com.example.testBA.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
        @Autowired
        private JavaMailSender javaMailSender;

        @Value("${spring.mail.username}")
        private String senderEmail;

        @Override
        public void sendEmailAlert(EmailDto emailDto) {
                try{
                        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                        simpleMailMessage.setFrom(senderEmail);
                        simpleMailMessage.setTo(emailDto.getRecipient());
                        simpleMailMessage.setText(emailDto.getMessageBody());
                        simpleMailMessage.setSubject(emailDto.getSubject());

                        javaMailSender.send(simpleMailMessage);
                        System.out.println("Mail has sent successfully!");

                } catch (MailException e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        public void sendEmailwithAttachments(EmailDto emailDto) {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper;
                try{
                    mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
                    mimeMessageHelper.setFrom(senderEmail);
                    mimeMessageHelper.setTo(emailDto.getRecipient());
                    mimeMessageHelper.setText(emailDto.getMessageBody());
                    mimeMessageHelper.setSubject(emailDto.getSubject());

                        FileSystemResource file = new FileSystemResource(new File(emailDto.getAttachment()));
                        mimeMessageHelper.addAttachment(file.getFilename(),file);
                        javaMailSender.send(mimeMessage);

                        log.info(file.getFilename() + " has been sent to user with email:" + emailDto.getRecipient());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
        }
}

