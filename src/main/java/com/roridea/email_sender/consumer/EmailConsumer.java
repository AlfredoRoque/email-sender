package com.roridea.email_sender.consumer;

import com.roridea.email_sender.dto.EmailEventDto;
import com.roridea.email_sender.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens for email events on the "email-topic" and processes them by sending emails using the EmailSenderService.
 * It is configured to retry sending emails up to 3 times with a backoff delay of 5 seconds in case of failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final EmailSenderService emailSenderService;

    /**
     * Listens for email events on the "email-topic" and processes them by sending emails.
     * If an error occurs while sending the email, it will retry up to 3 times with a backoff delay of 5 seconds.
     *
     * @param event the EmailEventDto containing the email event data to be processed
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 5000),
            dltTopicSuffix = "-dlt",
            retryTopicSuffix = "-retry"
    )
    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listen(EmailEventDto event) {
        log.info("Received email event for {}", event.getTo());
        emailSenderService.sendEmail(event);
    }
}
