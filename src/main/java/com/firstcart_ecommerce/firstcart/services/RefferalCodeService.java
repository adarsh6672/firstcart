package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.RefferalCode;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.RefferalCodeRepo;
import jakarta.persistence.Access;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefferalCodeService {

    @Autowired
    private RefferalCodeRepo refferalCodeRepo;

    public void generateRefferalCode(User user){
        String code= RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        RefferalCode refferalCode=new RefferalCode();
        refferalCode.setCode(code);
        refferalCode.setUser(user);
        refferalCodeRepo.save(refferalCode);
    }

    public boolean isUserHavingCode(User user){
      if(refferalCodeRepo.findByUser(user)!=null){
          return true;
      }
        return false;
    }

    public RefferalCode findcodebyuser(User user){
        return refferalCodeRepo.findByUser(user);
    }

    public boolean isCodeExist(String code) {
        List<RefferalCode> refferalCodes = refferalCodeRepo.findAll();
        for (RefferalCode refferalCode : refferalCodes) {
            if (refferalCode.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
    public int codeUsage(String code){
        List<RefferalCode> refferalCodes = refferalCodeRepo.findAll();
        for(RefferalCode refferalCode:refferalCodes){
            if(refferalCode.getCode().equals(code)){
                return refferalCode.getUsagecount();
            }
        }
        return -1;
    }
}
