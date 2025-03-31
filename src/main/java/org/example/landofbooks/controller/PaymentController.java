package org.example.landofbooks.controller;

import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.service.impl.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/create-payment", method = RequestMethod.POST)
    public Map<String, String> createPaymentRequest(Orders order) {
        // Create the payment request data
        return paymentService.createPaymentRequest(order);
    }
    @GetMapping("/payment-success")
    public String paymentSuccess() {
        // You can add logic to fetch details about the payment, if needed
        return "payment-success";  // This will render the payment-success.html page
    }
}
