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
import java.util.UUID;

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
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setTo(to);
            helper.setSubject("🔐 Your One-Time Password (OTP)");

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="max-width: 500px; margin: auto; background: #fff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">Your OTP Code</h2>
                    <p style="font-size: 16px;">Use the following code to proceed with your action:</p>
                    <div style="font-size: 32px; font-weight: bold; color: #4CAF50; margin: 20px 0;">%s</div>
                    <p>This code will expire shortly for your security.</p>
                    <p>If you didn’t request this code, please ignore this email.</p>
                    <br>
                    <p style="color: #999;">- The Paradise of Books Team</p>
                </div>
            </body>
            </html>
        """.formatted(otp);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSuccessEmail(String to, String bookId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setTo(to);
            helper.setSubject("📖 Book Review Status - " + bookId);

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">Book Review Status</h2>
                    <p>Dear Seller,</p>
                    <p>Your book with ID <strong>%s</strong> is currently <strong>under review</strong>.</p>
                    <p>We’ll get back to you within <strong>24 hours</strong> with an update.</p>
                    <p>Thank you for choosing Paradise of Books!</p>
                    <p style="margin-top: 30px;">Happy Reading! 📖✨</p>

                    <p style="color: #999;">- The Paradise of Books Team</p>
                </div>
            </body>
            </html>
        """.formatted(bookId);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send status email: " + e.getMessage(), e);
        }
    }


    @Override
    public void sendOrderMail(String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setTo(email);
            helper.setSubject("📚 Your Paradise of Books Order Receipt");

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">Thank you for your order! 📦</h2>
                    <p>Dear Customer,</p>
                    <p>Your order has been successfully placed. We're getting it ready and will ship it within <strong>4 business days</strong>.</p>
                    
                    <hr style="margin: 20px 0;">

                    <p>In the meantime, feel free to browse our <a href="https://Paradiseofbooks.com" style="color: #4CAF50;">store</a> for more literary treasures!</p>
                    <p style="margin-top: 30px;">Happy Reading! 📖✨</p>

                    <p style="color: #999;">- The Paradise of Books Team</p>

                    <div style="margin-top: 30px; font-size: 12px; color: #aaa;">
                        <p>This is an automated message. Please do not reply directly to this email.</p>
                    </div>
                </div>
            </body>
            </html>
        """;

            helper.setText(htmlContent, true); // true enables HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // or use a logger
            throw new RuntimeException("Failed to send order receipt email", e);
        }
    }

}
