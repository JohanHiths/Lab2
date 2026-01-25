package com.example.payment;

import java.sql.SQLException;
import java.util.Calendar;

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
    }

    public boolean processPayment(double amount) throws SQLException {
        // Anropar extern betaltjänst direkt med statisk API-nyckel
        PaymentApiResponse response = PaymentApi.charge(API_KEY, amount);

        // Skriver till databas direkt
        if (response.isSuccess()) {
            DatabaseConnection.getInstance()
                    .executeUpdate("INSERT INTO payments (amount, status) VALUES (" + amount + ", 'SUCCESS')");
        }

        // Skickar e-post direkt
        if (response.isSuccess()) {
            EmailService.sendPaymentConfirmation("user@example.com", amount);
        }

        return response.isSuccess();
    }
}