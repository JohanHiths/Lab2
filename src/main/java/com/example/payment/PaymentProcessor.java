package com.example.payment;

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

    public boolean processPayment(double amount) throws SQLException {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        PaymentApiResponse response = paymentGateway.charge(amount);

        // Skriver till databas direkt
        if (response.isSuccess()) {
            paymentRepository.saveSuccessfulPayment(amount);
                emailService.sendPaymentConfirmation("user@example.com", amount);
                return true;
        }

            return false;
        // Skickar e-post direkt

    }
}