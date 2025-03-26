package org.example.landofbooks.service.impl;

import org.example.landofbooks.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpServiceImpl implements OtpService {
    private final Map<String, OtpEntry> otpStorage = new HashMap<>();

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

    private static class OtpEntry {
        String otp;
        LocalDateTime expiryTime;

        OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
