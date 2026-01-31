package com.example.payment;

import java.math.BigDecimal;

//
public interface PaymentGateway {

    PaymentApiResponse charge(BigDecimal amount);
}
