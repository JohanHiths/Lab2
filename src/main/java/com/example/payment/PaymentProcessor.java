package com.example.payment;

import java.math.BigDecimal;
import java.sql.SQLException;


//
public class PaymentProcessor {
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private static final double MAX_AMOUNT = 100000.0;

    public PaymentProcessor(PaymentGateway paymentGateway,
                            PaymentRepository paymentRepository,
                            EmailService emailService) {

        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }//

    public boolean processPayment(double amount, String customerEmail) throws SQLException {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        if (amount <= 0 || amount > MAX_AMOUNT || customerEmail == null || customerEmail.isBlank()) {
            return false;
        }
        PaymentApiResponse response = paymentGateway.charge(BigDecimal.valueOf(amount));



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