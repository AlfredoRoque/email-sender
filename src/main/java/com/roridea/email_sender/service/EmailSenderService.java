package com.roridea.email_sender.service;

import com.roridea.email_sender.dto.EmailEventDto;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Service responsible for sending emails using the SendGrid API.
 * It constructs email messages based on the provided EmailEventDto and handles the communication with SendGrid to send the emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final SendGrid sendGrid;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    /**
     * Sends an email based on the provided EmailEventDto.
     *
     * @param event the EmailEventDto containing the email details such as recipient, subject, content, and optional attachment
     */
    public void sendEmail(EmailEventDto event) {

        Email from = new Email(fromEmail);
        Email to = new Email(event.getTo());

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(event.getSubject());

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);

        mail.addContent(new Content("text/html", event.getHtmlContent()));

        if (event.getAttachment() != null) {
            Attachments attachments = new Attachments();
            attachments.setFilename(event.getAttachmentName());
            attachments.setType("application/pdf");
            attachments.setDisposition("attachment");
            attachments.setContent(
                    Base64.getEncoder().encodeToString(event.getAttachment())
            );
            mail.addAttachments(attachments);
        }

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            log.info("Sending email to {}", event.getTo());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() != 202) {
                log.error("SendGrid error: {}", response.getBody());
                throw new RuntimeException("Error sending email");
            }

            log.info("Email successfully sent to {}", event.getTo());

        } catch (Exception e) {
            log.error("Email sending failed", e);
            throw new RuntimeException(e);
        }
    }
}