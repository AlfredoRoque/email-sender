package com.roridea.email_sender.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * ApplicationContextInitializer that loads Kafka SSL certificates from environment variables, decodes them from Base64, and writes them to temporary files.
 * This allows the application to use the SSL certificates for secure communication with Kafka brokers.
 */
public class KafkaSslInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            // Keystore
            String keystoreBase64 = System.getenv("KAFKA_KEYSTORE_BASE64");
            Files.write(Paths.get("/tmp/client.keystore.jks"), Base64.getDecoder().decode(keystoreBase64));

            // Truststore
            String truststoreBase64 = System.getenv("KAFKA_TRUSTSTORE_BASE64");
            Files.write(Paths.get("/tmp/client.truststore.jks"), Base64.getDecoder().decode(truststoreBase64));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Kafka SSL files", e);
        }
    }
}