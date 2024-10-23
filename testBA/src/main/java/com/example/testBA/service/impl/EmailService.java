package com.example.testBA.service.impl;

import com.example.testBA.dto.EmailDto;

public interface EmailService {
    void sendEmailAlert(EmailDto emailDto);
    void sendEmailwithAttachments(EmailDto emailDto);
}
