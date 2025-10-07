package com.demo.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.UUID;

@Configuration
public class ConsulConfig {

    @Value("${consul.host:localhost}")
    private String consulHost;

    @Value("${consul.port:8500}")
    private int consulPort;

    @Value("${service.name:borrow-service}")
    private String serviceName;

    @Value("${service.port:8080}")
    private int servicePort;

    @Value("${service.contextPath:/BorrowRecord}")
    private String contextPath;

    private ConsulClient consulClient;
    private String registeredServiceId;

    // Tạo bean ConsulClient để Spring có thể inject
    @Bean
    public ConsulClient consulClient() {
        return new ConsulClient(consulHost, consulPort);
    }

    @PostConstruct
    public void registerService() {
        try {
            // Sử dụng bean đã được tạo
            consulClient = consulClient();

            String serviceId = serviceName + "-" + UUID.randomUUID().toString().substring(0, 8);
            String serviceAddress = InetAddress.getLocalHost().getHostAddress();

            NewService service = new NewService();
            service.setId(serviceId);
            service.setName(serviceName);
            service.setAddress(serviceAddress);
            service.setPort(servicePort);

            // Health check
            NewService.Check check = new NewService.Check();
            check.setHttp("http://" + serviceAddress + ":" + servicePort + contextPath + "/health");
            check.setInterval("10s");
            check.setDeregisterCriticalServiceAfter("1m");
            service.setCheck(check);

            consulClient.agentServiceRegister(service);
            registeredServiceId = serviceId;

            System.out.printf("✅ Registered %s at %s:%d (contextPath=%s)%n",
                    serviceName, serviceAddress, servicePort, contextPath);
        } catch (Exception e) {
            System.err.println("❌ Failed to register with Consul: " + e.getMessage());
        }
    }

    @PreDestroy
    public void deregister() {
        try {
            if (consulClient != null && registeredServiceId != null) {
                consulClient.agentServiceDeregister(registeredServiceId);
                System.out.println("✅ Deregistered from Consul: " + registeredServiceId);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error deregistering from Consul: " + e.getMessage());
        }
    }
}