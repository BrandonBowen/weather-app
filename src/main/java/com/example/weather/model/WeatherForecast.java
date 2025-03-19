package com.example.weather.model;

import java.util.Map;

public class WeatherForecast {
    private String city;
    private Map<String, Temperature> dailyTemperatures;

    public WeatherForecast(String city, Map<String, Temperature> dailyTemperatures) {
        this.city = city;
        this.dailyTemperatures = dailyTemperatures;
    }

    public String getCity() {
        return city;
    }

    public Map<String, Temperature> getDailyTemperatures() {
        return dailyTemperatures;
    }

    public static class Temperature {
        private double high;
        private double low;

        public Temperature(double high, double low) {
            this.high = high;
            this.low = low;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }
    }
}
