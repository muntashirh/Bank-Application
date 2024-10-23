package com.example.testBA.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDto {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;

}
