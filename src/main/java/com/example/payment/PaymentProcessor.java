package com.example.payment;

public class PaymentProcessor {
    private PaymentRepository paymentRepository;
    private EmailService emailService;
    private PaymentGateway paymentGateway;


    public PaymentProcessor(EmailService emailService, PaymentRepository paymentRepository, String API_KEY, PaymentGateway paymentGateway) {

        this.emailService = emailService;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;



    }


    public boolean processPayment(double amount) {
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
