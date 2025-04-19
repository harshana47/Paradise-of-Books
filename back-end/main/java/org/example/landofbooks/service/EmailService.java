package org.example.landofbooks.service;


import java.util.UUID;

public interface EmailService {

    public String generateOTP();

    public void sendOtpEmail(String to);

    public void sendSuccessEmail(String to, String bookId);

    void sendOrderMail(String email);
}
