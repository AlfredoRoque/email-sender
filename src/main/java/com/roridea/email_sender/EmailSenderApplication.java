package com.roridea.email_sender;

import com.roridea.email_sender.config.KafkaSslInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class EmailSenderApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EmailSenderApplication.class)
				.initializers(new KafkaSslInitializer())
				.run(args);
	}

}
