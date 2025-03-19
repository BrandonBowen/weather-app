package com.example.weather.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.weather.model.ParsedAddress;

public class AddressParser {
    
    private static final Pattern ADDRESS_PATTERN = 
        Pattern.compile("^(.*),\\s*(.*),\\s*([A-Z]{2})\\s*(\\d{5}),\\s*([A-Z]{2})$");

    public static ParsedAddress parseAddress(String address) {
        Matcher matcher = ADDRESS_PATTERN.matcher(address);
        
        if (matcher.matches()) {
            String street = matcher.group(1);
            String city = matcher.group(2);
            String state = matcher.group(3);
            String zipCode = matcher.group(4);
            String country = matcher.group(5);
            
            return new ParsedAddress(street, city, state, zipCode, country);
        } else {
            throw new IllegalArgumentException("Invalid address format: " + address);
        }
    }
}