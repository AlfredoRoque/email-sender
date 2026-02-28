package com.roridea.email_sender.consumer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class KafkaCertLoader {

    @Value("${KAFKA_TRUSTSTORE_BASE64}")
    private String truststoreBase64;

    @Value("${KAFKA_KEYSTORE_BASE64}")
    private String keystoreBase64;

    @PostConstruct
    public void loadCerts() throws Exception {
        createFileFromBase64(keystoreBase64, "/tmp/client.keystore.jks");
        createFileFromBase64(truststoreBase64, "/tmp/client.truststore.jks");
    }

    private void createFileFromBase64(String base64, String path) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(base64);
        Files.write(Paths.get(path), decoded);
    }
}