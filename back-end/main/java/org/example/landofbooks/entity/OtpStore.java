package org.example.landofbooks.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class OtpStore {
    private final Map<String, String> otpMap = new HashMap<>();

    public void storeOTP(String email, String otp) {
        otpMap.put(email, otp);
    }

    public String getOTP(String email) {
        return otpMap.get(email);
    }

    public void removeOTP(String email) {
        otpMap.remove(email);
    }
}
