package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.AddressRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private HttpServletResponse response;



    @Autowired
    private UserRepo userRepo;
    public void deleteAddressById(Long id) {
        addressRepo.deleteById(id);
    }


    public void addAddressToUser(String email, Address address) throws IOException {
        List<Address> addresses = new ArrayList<>();
        User user = userRepo.findByEmail(email);
        if (user != null) {
            try {
                if(address.isDefaultAddress()){
                    Address oldDefaultAddress = user.getAddresses().stream()
                            .filter(Address::isDefaultAddress)
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("Default address not found"));

                    oldDefaultAddress.setDefaultAddress(false);
                } else if (user.getAddresses().isEmpty()) {
                    address.setDefaultAddress(true);
                }
            }catch (EntityNotFoundException e){
                response.sendRedirect("/user/profile/address");
            }



            address.setUser(user);
            addressRepo.save(address);
        }
    }
}
