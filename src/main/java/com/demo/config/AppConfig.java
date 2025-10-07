package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.demo")
public class AppConfig{
    @Bean
    public ConsulConfig consulConfig() {
        return new ConsulConfig(); 
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}