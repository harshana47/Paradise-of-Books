package org.example.landofbooks.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.landofbooks.service.EmailService;
import org.example.landofbooks.service.OtpService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final OtpService otpService;

    @Override
    public String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public void sendOtpEmail(String to) {
        String otp = generateOTP();
        otpService.storeOtp(to, otp);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(to);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP code is: " + otp);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }
    @Override
    public void sendSuccessEmail(String to, String bookId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Book Status for - " + bookId);
        message.setText("Dear Seller, Your book (" + bookId + ") is under review. we will notify you within 24h. Happy Reading📖✨!");

        mailSender.send(message);
    }
}
