package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.dto.OtpRequest;
import com.firstcart_ecommerce.firstcart.dto.OtpResponseDto;
import com.firstcart_ecommerce.firstcart.dto.OtpValidationRequest;
import com.firstcart_ecommerce.firstcart.services.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@Slf4j
public class OtpController {
    @Autowired
    private SmsService smsService;
    @GetMapping("/process")
    public String processSMS(){
        return "SMS SENT....";
    }
    @PostMapping("/sent-otp")
    public OtpResponseDto sendOtp(@RequestBody OtpRequest otpRequest){
            log.info("inside sendOtp :: "+otpRequest.getUsername());
            return smsService.sendSMS(otpRequest);
    }
    @PostMapping("/validate-otp")
    public String validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        log.info("inside validateOtp :: "+otpValidationRequest.getUsername()+" "+otpValidationRequest.getOtpNumber());
        return smsService.validateOtp(otpValidationRequest);
    }
}
