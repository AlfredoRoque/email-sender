package com.roridea.email_sender.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up SendGrid email service integration.
 * It reads the SendGrid API key from the application properties and creates a SendGrid bean for use in the application.
 */
@Configuration
public class SendGridConfig {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    /**
     * Configures and returns a SendGrid instance using the provided API key.
     *
     * @return a SendGrid instance configured with the API key
     */
    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(apiKey);
    }
}
