package com.example.testBA;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(

		info = @Info(
				title = "Bank Application",
				description = "Backend REST APIs for Bank Application",
				version = "v1.0",
				contact = @Contact(
						name = "Muntashir Hossain",
						email = "muntashirhossain1@gmail.com",
						url = "https://github.com/muntashirh/BankApplication"
				),
				license = @License(
						name = "Back Application",
						url = "https://github.com/muntashirh/BankApplication"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Bank Application Documentation",
				url = "https://github.com/muntashirh/BankApplication"
		)
)
public class TestBaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestBaApplication.class, args);
	}

}
