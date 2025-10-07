package com.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ValidationService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    public void assertUserAndBookExist(String userId, String bookId) {
        validateEntity("user-service", userId, "/api/users/");
        validateEntity("book-service", bookId, "/api/books/");
    }

    /**
     * Hàm dùng chung để kiểm tra user hoặc book có tồn tại hay không.
     */
    private void validateEntity(String serviceName, String entityId, String apiPath) {
        String baseUrl = getBaseUrl(serviceName);
        String contextPath = discoveryService.inferContextPath(serviceName);
        String url = baseUrl + contextPath + apiPath + entityId;

        log.info("Validating {} via URL: {}", serviceName, url);

        // Forward Authorization header from incoming request if present
        HttpHeaders headers = new HttpHeaders();
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes) {
            HttpServletRequest req = ((ServletRequestAttributes) attrs).getRequest();
            String auth = req.getHeader("Authorization");
            if (auth != null && !auth.isEmpty()) {
                headers.set("Authorization", auth);
            }
        }

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                if (response.getStatusCode().value() == 401) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized calling " + serviceName);
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + serviceName + " id");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized calling " + serviceName);
        } catch (RestClientResponseException e) {
            // Other 4xx/5xx from downstream
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + serviceName + " id");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + serviceName + " id");
        }
        
        
    }

    /**
     * Lấy URL cơ sở (base URL) từ biến môi trường hoặc từ Consul.
     */
    private String getBaseUrl(String serviceName) {
        String envKey = serviceName.toUpperCase().replace("-", "_") + "_URL";
        String envUrl = System.getenv().getOrDefault(envKey, "");

        if (envUrl != null && !envUrl.isEmpty()) {
            return envUrl;
        }

        String consulUrl = discoveryService.resolveServiceBaseUrl(serviceName);
        if (consulUrl == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot resolve " + serviceName);
        }

        return consulUrl;
    }
}
