package com.example.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.weather.exception.WeatherNotFoundException;
import com.example.weather.model.WeatherResponse;
import com.example.weather.model.WeatherResponseWrapper;

@Service
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;
    @Value("${openweather.api.url}")
    private String baseApiUrl;

    @Autowired
    private CacheManager cacheManager;
    private final RestTemplate restTemplate;

    public String getApiUrl(String zip, String country) {
        return String.format("%s?zip=%s,%s&appid=%s&units=imperial", baseApiUrl, zip, country, apiKey);
    }

    @Autowired
    public WeatherService(CacheManager cacheManager, RestTemplate restTemplate) {
        this.cacheManager = cacheManager;
        this.restTemplate = restTemplate;
    }
    
    
    public WeatherResponseWrapper getWeather(String country, String zipCode) {
        String url = getApiUrl(zipCode, country);
        System.out.println("URL: " + url);
        Cache weatherCache = cacheManager.getCache("weatherCache");
        // Check if the data is already cached
        WeatherResponse cachedResponse = weatherCache != null ? weatherCache.get(zipCode, WeatherResponse.class) : null;
        boolean isCached = cachedResponse != null;

        WeatherResponse response;
        if (isCached) {
            response = cachedResponse;
        } else {
            // Fetch from API if not in cache
            try {
                response = restTemplate.getForObject(url, WeatherResponse.class);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new WeatherNotFoundException("Weather data not found for provided input. Please ensure your input is valid ");
                }
                else throw e;
            }
            // Store the response in cache
            if (weatherCache != null && response != null) {
                weatherCache.put(zipCode, response);
            }
        }

        return new WeatherResponseWrapper(response, isCached);
    }
}