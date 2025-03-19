package com.example.weather.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import com.example.weather.exception.WeatherNotFoundException;
import com.example.weather.model.WeatherResponseWrapper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RestTemplate restTemplate;

    private final String zipCode = "78660";
    private final String country = "US";

    // Ensure cache is cleared before each test
    @BeforeEach
    void setUp() {
        Cache weatherCache = cacheManager.getCache("weatherCache");
        assertNotNull(weatherCache);
        weatherCache.clear(); // Clear cache before each test
    }

    // Ensure that cacheing mechanism is working as expected
    // It should attach indicator to cached responses
    // So, if cache is cleared, it should fetch from API again and indicate not cached
    @Test
    void testGetWeather_CacheBehavior() {
        Cache weatherCache = cacheManager.getCache("weatherCache");
        assertNotNull(weatherCache);
        
        // First call: should fetch from API and not be cached initially
        WeatherResponseWrapper firstCall = weatherService.getWeather(country, zipCode);
        assertNotNull(firstCall);
        assertFalse(firstCall.isCached());
        assertNotNull(weatherCache.get(zipCode)); // Verify it was cached
        
        // Second call: should retrieve from cache
        WeatherResponseWrapper secondCall = weatherService.getWeather(country, zipCode);
        assertNotNull(secondCall);
        assertTrue(secondCall.isCached());
        
        // Clear cache and call again: should fetch from API again
        weatherCache.clear();
        WeatherResponseWrapper thirdCall = weatherService.getWeather(country, zipCode);
        assertNotNull(thirdCall);
        assertFalse(thirdCall.isCached());
    }

    // The api should respond with Pflugerville for the city
    // Since that is what city corresponds to the zip code
    @Test
    void testGetWeather_CorrectCity() {
        Cache weatherCache = cacheManager.getCache("weatherCache");
        assertNotNull(weatherCache);
        
        // First call: should fetch from API and not be cached initially
        // Should have Pflugerville in response for city
        // Should also populate relavant view fields
        WeatherResponseWrapper firstCall = weatherService.getWeather(country, zipCode);
        assertNotNull(firstCall);
        assertEquals(firstCall.getWeatherResponse().getName(), "Pflugerville");
        assertNotNull(firstCall.getWeatherResponse().getMain());
        assertNotNull(firstCall.getWeatherResponse().getMain().getTemp());
        assertNotNull(firstCall.getWeatherResponse().getMain().getFeels_like());
        assertNotNull(firstCall.getWeatherResponse().getMain().getTemp_max());
        assertNotNull(firstCall.getWeatherResponse().getMain().getTemp_min());
        assertNotNull(firstCall.getWeatherResponse().getWeather());
        assertTrue(firstCall.getWeatherResponse().getWeather().length > 0);
        assertNotNull(firstCall.getWeatherResponse().getWeather()[0].getDescription());
        
    }

    // The api should respond with a 404 status code
    // Since the zip code is invalid
    @Test
    void testGetWeather_NotFoundException() {
        assertThrows(WeatherNotFoundException.class, () -> weatherService.getWeather(country, "00000"));
    }
}
