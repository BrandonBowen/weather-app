package com.example.weather.controller;

import com.example.weather.exception.GlobalExceptionHandler;
import com.example.weather.model.WeatherResponse;
import com.example.weather.model.WeatherResponseWrapper;
import com.example.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configure a view resolver to map "weather" to an HTML template
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(weatherController)
                .setViewResolvers(viewResolver)
                .setControllerAdvice(new GlobalExceptionHandler()) 
                .build();
    }

    // Weather view should be returned when no address is provided
    @Test
    void testGetWeather_noAddress() throws Exception {
        // When no address is provided, the controller simply returns the "weather" view.
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"));
    }

    // Weather view should be returned when an valid address is provided
    @Test
    void testGetWeather_withValidAddress() throws Exception {
        // Valid address that matches your parser's regex format
        String address = "6900 West Parmer Lane, Austin, TX 78727, US";
        String country = "US";
        String zipCode = "78727";
        
        // Create a dummy WeatherResponse instance
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setName("Austin");
        weatherResponse.setCod(200);

        WeatherResponseWrapper mockWeatherResponseWrapper = new WeatherResponseWrapper(weatherResponse, false);
        when(weatherService.getWeather(country, zipCode)).thenReturn(mockWeatherResponseWrapper);

        mockMvc.perform(get("/weather").param("address", address))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("weather", weatherResponse))
                .andExpect(model().attribute("isCached", false))
                .andExpect(model().attribute("address", address));

        verify(weatherService, times(1)).getWeather(country, zipCode);
    }

    // If address cannot be paresed, then the global handler should
    // return a bad request code and message about invalid form data
    @Test
    void testGetWeather_invalidAddressFormat() throws Exception {
        String invalidAddress = "Invalid address format";
    
        mockMvc.perform(get("/weather").param("address", invalidAddress))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid form data!"));
    }

    // When address is an empty string, it should be treated the same as no address provided
    @Test
    void testGetWeather_withEmptyAddress() throws Exception {
        // When address is empty, it should be treated the same as no address provided.
        mockMvc.perform(get("/weather").param("address", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"));
    }
}
