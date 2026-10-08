package com.smartsociety.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(UserServiceApplication.class, args);
    }

    private static void loadEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("../.env");
        }
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eq = line.indexOf('=');
                    if (eq > 0) {
                        String key = line.substring(0, eq).trim();
                        String val = line.substring(eq + 1).trim();
                        if (System.getenv(key) == null && System.getProperty(key) == null) {
                            System.setProperty(key, val);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
