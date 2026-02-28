package com.roridea.email_sender.consumer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class KafkaCertLoader {

    @PostConstruct
    public void init() throws Exception {
        // Keystore
        String keystoreBase64 = System.getenv("KAFKA_KEYSTORE_BASE64");
        Files.write(Paths.get("/tmp/client.keystore.jks"), Base64.getDecoder().decode(keystoreBase64));

        // Truststore
        String truststoreBase64 = System.getenv("KAFKA_TRUSTSTORE_BASE64");
        Files.write(Paths.get("/tmp/client.truststore.jks"), Base64.getDecoder().decode(truststoreBase64));
    }
}