package org.example.landofbooks.service.impl;

import org.example.landofbooks.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {
    private final Map<String, OtpEntry> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> lastOtpRequestTime = new HashMap<>(); // New map for request timestamps

    @Override
    public void storeOtp(String email, String otp) {
        otpStorage.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5))); // OTP expires in 5 minutes
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null || LocalDateTime.now().isAfter(entry.expiryTime)) {
            return false; // OTP expired or not found
        }
        return entry.otp.equals(otp);
    }

    // Method to check if OTP can be resent
    public boolean canResendOtp(String email) {
        LocalDateTime lastRequestTime = lastOtpRequestTime.get(email);

        if (lastRequestTime == null) {
            return true; // No previous request, allow OTP resend
        }

        // Check if 30 seconds have passed since the last request
        return lastRequestTime.plusSeconds(30).isBefore(LocalDateTime.now());
    }

    // Store the time of the last OTP request
    public void storeLastOtpRequestTime(String email) {
        lastOtpRequestTime.put(email, LocalDateTime.now());
    }

    // Resend OTP function
    @Override
    public void resendOtp(String email) {
        if (!canResendOtp(email)) {
            throw new RuntimeException("You can only request OTP after 30 seconds.");
        }

        // Generate and store new OTP
        String otp = generateOTP();
        storeOtp(email, otp);
        storeLastOtpRequestTime(email);
    }

    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private static class OtpEntry {
        String otp;
        LocalDateTime expiryTime;

        OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
