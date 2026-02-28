package com.roridea.email_sender.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for email events, used to encapsulate the data required to send an email, including recipient, subject, content, and optional attachment.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailEventDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
    private String htmlContent;
    private byte[] attachment;
    private String attachmentName;
}
