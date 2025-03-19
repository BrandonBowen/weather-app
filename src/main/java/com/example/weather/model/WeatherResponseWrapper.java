package com.example.weather.model;

public class WeatherResponseWrapper {
    private WeatherResponse weatherResponse;
    private boolean isCached;

    public WeatherResponseWrapper(WeatherResponse weatherResponse, boolean isCached) {
        this.weatherResponse = weatherResponse;
        this.isCached = isCached;
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public boolean isCached() {
        return isCached;
    }
}
