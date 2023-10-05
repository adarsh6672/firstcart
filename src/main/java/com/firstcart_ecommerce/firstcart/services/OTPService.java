package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class OTPService {

    private final TwilioConfig twilioConfig;


    @Autowired
    public OTPService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }


    // Generate an OTP of 4 digits
    public String generateOTP() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void sendOTP(String phoneNumber, String otp) {
        Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("+919400686672"),
                new com.twilio.type.PhoneNumber(twilioConfig.getPhoneNumber()),
                "your otp for firstcart signup is  "+otp)

                .create();
        System.out.println(message.getSid());
    }
}
