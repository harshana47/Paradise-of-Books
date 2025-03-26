package org.example.landofbooks.service;

public interface OtpService {
    public void storeOtp(String email, String otp);
    public boolean verifyOtp(String email, String otp);
}
