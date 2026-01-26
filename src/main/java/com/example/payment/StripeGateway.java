package com.example.payment;
//
public class StripeGateway implements PaymentGateway{
    private final String apiKey;
    public StripeGateway(String apiKey) {
        this.apiKey = apiKey;
    }
    public PaymentApiResponse charge(double amount) {

        return charge(amount);
    }




}
