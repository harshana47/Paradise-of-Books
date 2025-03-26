package org.example.landofbooks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Set Gmail SMTP server details
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // Port for TLS
        mailSender.setUsername("timberntaste@gmail.com"); // Replace with your email address
        mailSender.setPassword("pqqj vgon czew gthh"); // Use App Password if 2FA is enabled

        // Set SMTP properties for TLS connection
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for secure connection
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Trust the Gmail SMTP server

        return mailSender; // Return configured mailSender
    }
}
