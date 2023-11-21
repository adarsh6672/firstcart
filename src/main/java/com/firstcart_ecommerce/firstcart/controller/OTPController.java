package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.RefferalCode;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.model.Wallet;
import com.firstcart_ecommerce.firstcart.repository.RefferalCodeRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.repository.WalletRepo;
import com.firstcart_ecommerce.firstcart.services.OTPService;
import com.firstcart_ecommerce.firstcart.services.RefferalCodeService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import com.firstcart_ecommerce.firstcart.services.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OTPController {
    @Autowired
    private OTPService otpService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RefferalCodeService refferalCodeService;

    @Autowired
    private RefferalCodeRepo refferalCodeRepo;

    @Autowired
    private UserService userService;

    @PostMapping("/sendOTP")
    public String sendOTP(@ModelAttribute User user,
                          @RequestParam(value = "refferalCode",required = false)String code,
                          Model model,HttpSession session) {
        System.out.println(code);
        if((code != null && !code.isEmpty())){
            int usage=refferalCodeService.codeUsage(code);
            if(usage==-1){
                session.setAttribute("msg","Invalid Refferal Code...!");
                return "redirect:/register";

            } else if (usage>=2) {
                session.setAttribute("msg","Refferal Code Maximum Limit Reached...!");
                return "redirect:/register";
            } else {
                session.setAttribute("code",code);
            }

        }
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
        String code=(String) session.getAttribute("code");
        if (otp.equals(realotp)) {
            System.out.println("otp veriied"+otp+realotp);
            System.out.println(user.getName());
            System.out.println(user.getPassword());
            User u=userService.saveUser(user);
            session.removeAttribute("user");
            session.removeAttribute("user");
            if(code!=null){
                RefferalCode rc=refferalCodeRepo.findByCode(code);
                rc.setUsagecount(rc.getUsagecount()+1);
                refferalCodeRepo.save(rc);
                User puser=rc.getUser();
                Wallet wallet=walletRepo.findByUser(puser);
                wallet.setAmount(wallet.getAmount()+200);
                walletRepo.save(wallet);
                Wallet walletnewuser=walletService.getOrCreateUserWallet(user);
                walletnewuser.setAmount(200.0);
                walletRepo.save(walletnewuser);


            }

            if(u!=null){
                System.out.println("successfuly saved");
                session.setAttribute("msg","REGISTRATION SUCCESSFUL..!");
            }else {
                System.out.println("something went wrong");
                session.setAttribute("msg","OOPS..! SOMETHING WENT WRONG");
            }
            return "redirect:/login";
        } else {
            System.out.println("wrong otp");
            session.setAttribute("msg","OOPS..! OTP VERIFICATION FAILED...!");
            return "redirect:/register";
        }
    }
}
