package org.example.landofbooks.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.example.landofbooks.entity.Orders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.secret}")
    private String secretKey;

    public Map<String, String> createPaymentRequest(Orders order) {
        Map<String, String> params = new HashMap<>();
        System.out.println("Merchant ID: " + merchantId);
        System.out.println("Secret Key: " + secretKey);
        params.put("merchant_id", merchantId);
        params.put("return_url", "http://localhost:8080/api/v1/payment-success");  // Update for local testing
        params.put("cancel_url", "http://localhost:8080/payment-cancel");    // Update for local testing
        params.put("notify_url", "http://localhost:8080/api/v1/payment-webhook");  // Update for local testing
        params.put("order_id", String.valueOf(order.getOid()));
        params.put("items", "Book Purchase");
        params.put("currency", "LKR");
        params.put("amount", String.format("%.2f", order.getTotalPrice()));

        // Generate hash
        String hash = generatePaymentHash(params);
        params.put("hash", hash);

        return params;
    }
    private String generatePaymentHash(Map<String, String> params) {
        String data = String.join("|",
                params.get("merchant_id"),
                params.get("order_id"),
                params.get("amount"),
                params.get("currency"),
                // Adding more fields if necessary for hash calculation
                params.get("items"));

        return HmacUtils.hmacSha256Hex(secretKey, data);
    }

}
