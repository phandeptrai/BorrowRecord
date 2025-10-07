package com.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AuthValidatorService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private RestTemplate restTemplate;

    public String validateToken(String token) {
        // Lấy địa chỉ AuthService qua Consul
        String baseUrl = discoveryService.resolveServiceBaseUrl("auth-service");
        if (baseUrl == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot find AuthService");
        }

        String url = baseUrl + "/AuthService/auth/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object sub = response.getBody().get("sub");
                return sub != null ? sub.toString() : null;
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
}
