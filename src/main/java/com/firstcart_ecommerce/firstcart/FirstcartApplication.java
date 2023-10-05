package com.firstcart_ecommerce.firstcart;

import com.firstcart_ecommerce.firstcart.config.TwilioConfig;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class FirstcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstcartApplication.class, args);
	}

}
