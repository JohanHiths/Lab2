package com.example.payment;

import java.math.BigDecimal;
import java.sql.SQLException;


//
public class PaymentProcessor {
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;


    public PaymentProcessor(PaymentGateway paymentGateway,
                            PaymentRepository paymentRepository,
                            EmailService emailService) {

        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }//

    public boolean processPayment(double amount, String customerEmail) throws SQLException {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        PaymentApiResponse response = paymentGateway.charge(BigDecimal.valueOf(amount));

        if (customerEmail == null || customerEmail.isBlank()) {
            return false;
        }

        // Skriver till databas direkt
        if (response.isSuccess()) {
            paymentRepository.saveSuccessfulPayment(amount);
                emailService.sendPaymentConfirmation(customerEmail, amount);
                return true;
        }

            return false;
        // Skickar e-post direkt

    }
}