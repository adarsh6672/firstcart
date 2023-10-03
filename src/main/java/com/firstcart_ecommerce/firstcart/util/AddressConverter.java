package com.firstcart_ecommerce.firstcart.util;

import com.firstcart_ecommerce.firstcart.model.Address;

public class AddressConverter {

    public static Address convertStringToAddress(String addressString) {
        Address address = new Address();

        return address;
    }

    public static String convertAddressToString(Address address) {
        // Create a formatted string that represents the address
        String formattedAddress = String.format(
                "Full Name: %s, Mobile: %s, Pin: %s, Building: %s, Street: %s, City: %s, State: %s, Country: %s",
                address.getFullName(),
                address.getMobile(),
                address.getPinCode(),
                address.getHouseNo(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry()
        );

        return formattedAddress;
    }
}
