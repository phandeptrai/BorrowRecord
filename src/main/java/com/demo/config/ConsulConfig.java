package com.demo.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.net.InetAddress;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulConfig {

    private ConsulClient consulClient;
    private String registeredServiceId;

    @PostConstruct
    public void registerService() {
        // Lấy host/port Consul từ env, mặc định localhost
        String consulHost = System.getenv().getOrDefault("CONSUL_HOST", "localhost");
        int consulPort = Integer.parseInt(System.getenv().getOrDefault("CONSUL_PORT", "8500"));

        consulClient = new ConsulClient(consulHost, consulPort);

        // Lấy thông tin service từ env
        String serviceName = System.getenv().getOrDefault("SERVICE_NAME", "borrow-service");
        String servicePortStr = System.getenv().getOrDefault("SERVICE_PORT", "8080");
        int servicePort = Integer.parseInt(servicePortStr);

        // Tạo ID duy nhất cho mỗi instance
        String serviceId = serviceName + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Lấy địa chỉ host của máy, fallback 127.0.0.1
        String serviceAddress;
        try {
            serviceAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            serviceAddress = System.getenv().getOrDefault("SERVICE_ADDRESS", "127.0.0.1");
        }

        // Tạo cấu hình service cho Consul
        NewService service = new NewService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setAddress(serviceAddress);
        service.setPort(servicePort);

        // Health check
        NewService.Check check = new NewService.Check();
        check.setHttp("http://" + serviceAddress + ":" + servicePort + "/BorrowRecord/health");
        check.setInterval("10s");
        check.setDeregisterCriticalServiceAfter("1m"); // tự remove nếu fail
        service.setCheck(check);

        // Đăng ký service
        consulClient.agentServiceRegister(service);
        registeredServiceId = serviceId;

        System.out.println("✅ Registered service to Consul: id=" + serviceId +
                " address=" + serviceAddress + ":" + servicePort);
    }

    @PreDestroy
    public void deregister() {
        try {
            if (consulClient != null && registeredServiceId != null) {
                consulClient.agentServiceDeregister(registeredServiceId);
                System.out.println("✅ Deregistered service from Consul: " + registeredServiceId);
            }
        } catch (Exception e) {
            System.err.println("Error deregistering from Consul: " + e.getMessage());
        }
    }
}
