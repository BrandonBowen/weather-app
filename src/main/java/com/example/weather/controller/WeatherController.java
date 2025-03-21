package com.example.weather.controller;

import com.example.weather.model.ParsedAddress;
import com.example.weather.model.WeatherResponseWrapper;
import com.example.weather.service.WeatherService;
import com.example.weather.util.AddressParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam(required = false) String address, Model model) {
        // Show only the form if no address is provided
        if (address == null || address.isEmpty()) {
            return "weather";  // Only show the form
        }

        // Validate Address format and extract country and zip code
        ParsedAddress parsedAddress = AddressParser.parseAddress(address);
        String country = parsedAddress.getCountry();
        String zipCode = parsedAddress.getZipCode();

        // Get weather data if address is provided
        WeatherResponseWrapper weatherResponseWrapper = weatherService.getWeather(country, zipCode);
        model.addAttribute("weather", weatherResponseWrapper.getWeatherResponse());
        model.addAttribute("isCached", weatherResponseWrapper.isCached());
        model.addAttribute("address", address); // Retain the address in the input field
        return "weather";  // Show weather data
    }
}
