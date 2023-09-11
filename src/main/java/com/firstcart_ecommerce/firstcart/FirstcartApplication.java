package com.firstcart_ecommerce.firstcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class FirstcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstcartApplication.class, args);
	}

}
