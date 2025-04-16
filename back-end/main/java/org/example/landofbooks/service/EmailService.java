package org.example.landofbooks.service;


public interface EmailService {

    public String generateOTP();

    public void sendOtpEmail(String to);

    public void sendSuccessEmail(String to, String bookId);
}
