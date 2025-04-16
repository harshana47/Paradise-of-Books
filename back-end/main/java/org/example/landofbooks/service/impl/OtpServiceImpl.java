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
    private final Map<String, LocalDateTime> lastOtpRequestTime = new HashMap<>();

    @Override
    public void storeOtp(String email, String otp) {
        otpStorage.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null || LocalDateTime.now().isAfter(entry.expiryTime)) {
            return false;
        }
        return entry.otp.equals(otp);
    }

    public boolean canResendOtp(String email) {
        LocalDateTime lastRequestTime = lastOtpRequestTime.get(email);

        if (lastRequestTime == null) {
            return true;
        }

        return lastRequestTime.plusSeconds(30).isBefore(LocalDateTime.now());
    }

    public void storeLastOtpRequestTime(String email) {
        lastOtpRequestTime.put(email, LocalDateTime.now());
    }

    @Override
    public void resendOtp(String email) {
        if (!canResendOtp(email)) {
            throw new RuntimeException("You can only request OTP after 30 seconds.");
        }

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
