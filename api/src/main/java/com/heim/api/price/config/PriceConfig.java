package com.heim.api.price.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PriceConfig {
    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
