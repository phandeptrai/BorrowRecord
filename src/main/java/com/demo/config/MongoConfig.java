package com.demo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@EnableMongoRepositories(basePackages = "com.demo.repository")
public class MongoConfig {

    private Properties loadMongoProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mongo.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("Không tìm thấy file mongo.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file mongo.properties", e);
        }
        return props;
    }

    @Bean
    public MongoClient mongoClient() {
        Properties props = loadMongoProperties();
        String connectionString = props.getProperty("mongo.connectionString");
        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        Properties props = loadMongoProperties();
        String database = props.getProperty("mongo.database");
        return new MongoTemplate(mongoClient(), database);
    }
}
