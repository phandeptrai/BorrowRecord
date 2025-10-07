package com.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.model.HealthService;

@Service
public class DiscoveryService {

    @Autowired
    private ConsulClient consulClient;

    // Lấy từ file cấu hình
    @Value("${service.user.contextPath:/UserService}")
    private String userContextPath;

    @Value("${service.book.contextPath:/BookServices}")
    private String bookContextPath;

    @Value("${service.auth.contextPath:/AuthService}")
    private String authContextPath;
    public String resolveServiceBaseUrl(String serviceName) {
        List<HealthService> services = consulClient.getHealthServices(serviceName, true, null).getValue();
        if (services == null || services.isEmpty()) {
            return null;
        }

        var svc = services.get(0).getService();
        String address = svc.getAddress();
        Integer port = svc.getPort();

        if (address == null || address.isEmpty() || port == null) {
            return null;
        }

        return "http://" + address + ":" + port;
    }

    public String inferContextPath(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "user-service":
                return userContextPath;
            case "book-service":
                return bookContextPath;
            case "auth-service":
                return authContextPath;
            default:
                return "";
        }
    }
}
