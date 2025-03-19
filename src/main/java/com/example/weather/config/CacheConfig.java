package com.example.weather.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Value("${weather.cache.expiration.minutes}")
    private long cacheExpirationMinutes;
    @Value("${weather.cache.expiration.maxSize}")
    private long cacheMaxSize;

    @Bean
    public CacheManager cacheManager() {
        System.out.println("Cache expiration time: " + cacheExpirationMinutes);
        System.out.println("cacheMaxSize: " + cacheMaxSize);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("weatherCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(cacheExpirationMinutes, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}