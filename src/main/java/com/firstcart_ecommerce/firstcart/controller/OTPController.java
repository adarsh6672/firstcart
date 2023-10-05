package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.OTPService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OTPController {
    @Autowired
    private OTPService otpService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @PostMapping("/sendOTP")
    public String sendOTP(@ModelAttribute User user, Model model,HttpSession session) {
        System.out.println(user.getName());
        String otp = otpService.generateOTP();
        otpService.sendOTP(user.getPhone(), otp);
        session.setAttribute("user", user);

        // Store the OTP in the model to verify it later
        session.setAttribute("otpreal", otp);

        // Redirect to the verify OTP page
        return "verifyOTP";
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam String otp, HttpSession session) {
        String realotp= (String) session.getAttribute("otpreal");
        User user = (User) session.getAttribute("user");
        if (otp.equals(realotp)) {
            System.out.println("otp veriied"+otp+realotp);
            System.out.println(user.getName());
            System.out.println(user.getPassword());
            User u=userService.saveUser(user);
            session.removeAttribute("user");
            session.removeAttribute("user");


            if(u!=null){
                System.out.println("successfuly saved");
                session.setAttribute("msg","REGISTRATION SUCCESSFUL..!");
            }else {
                System.out.println("something went wrong");
                session.setAttribute("msg","OOPS..! SOMETHING WENT WRONG");
            }
            return "redirect:/register";
        } else {
            System.out.println("wrong otp");
            session.setAttribute("msg","OOPS..! OTP VERIFICATION FAILED...!");
            return "redirect:/register";
        }
    }
}
