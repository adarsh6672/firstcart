package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.OTPService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {


    @Autowired
    private OTPService otpService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/forgotpassword")
    public String forgotPassword(@ModelAttribute ("mailId")String mailId , HttpSession session){
        User user=userRepo.findByEmail(mailId);
        if(user!=null) {
            String otp = otpService.generateOTP();
            otpService.sendOTP(user.getPhone(), otp);
            String phonenmbr = user.getPhone();
            session.setAttribute("otp-real", otp);
            session.setAttribute("mailId", mailId);
            session.setAttribute("msg", "OTP sent to registered Mobile Number XXXXXX" + phonenmbr.substring(phonenmbr.length() - 4));
            return "forgotpassword";
        }
        session.setAttribute("msg","User Not Found with "+mailId);
        return "/login";

    }

    @PostMapping("/forgotpassword/verifyOTP")
    public String forgotPasswordOTP(@RequestParam String otp, HttpSession session) {
        String realotp= (String) session.getAttribute("otp-real");
        String mailid = (String) session.getAttribute("mailId");
        if (otp.equals(realotp)){
            System.out.println(realotp);
            System.out.println(otp);
            session.setAttribute("mailId",mailid);
            return "resetpassword";
        }
        session.setAttribute("msg","OTP verification Failed .. Please try again.");
        return "forgotpassword";

    }

    @PostMapping("/forgotpassword/update")
    public String updatePassword(@RequestParam String newPassword,
                                 HttpSession session){
        String mailid = (String) session.getAttribute("mailId");
        User user=userRepo.findByEmail(mailid);
        if(user !=null) {
            String password = passwordEncoder.encode(newPassword);
            user.setPassword(password);
            userRepo.save(user);
            session.setAttribute("msg", "Password Reset successfully.. Please LogIn");
            return "/login";
        }
        session.setAttribute("msg","Password Reset Failed ... !");
        return "/login";
    }


}
