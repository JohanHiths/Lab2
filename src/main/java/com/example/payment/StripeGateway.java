package com.example.payment;

import java.math.BigDecimal;

//
public class StripeGateway implements PaymentGateway{
    private final String apiKey;
    public StripeGateway(String apiKey) {
        this.apiKey = apiKey;
    }


    @Override
    public PaymentApiResponse charge(BigDecimal amount) {
        return new PaymentApiResponse(
                amount.compareTo(BigDecimal.ZERO) > 0
        );
    }


}
