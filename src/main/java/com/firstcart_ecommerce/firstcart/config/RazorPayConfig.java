package com.firstcart_ecommerce.firstcart.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rzp")
@Data
public class RazorPayConfig {
    private String key_id;
    private String key_secret;
    private String currency;
    private String company_name;


}
