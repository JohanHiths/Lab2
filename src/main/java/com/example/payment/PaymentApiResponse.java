package com.example.payment;

/// /
public record PaymentApiResponse(boolean success) {


    public boolean isSuccess() {
        return success;
    }
}

