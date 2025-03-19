package com.example.weather.model;

public class ParsedAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public ParsedAddress(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }
}
