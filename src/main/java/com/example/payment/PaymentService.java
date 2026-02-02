package com.example.payment;


import java.sql.SQLException;

/// /
public interface PaymentService {

    PaymentApiResponse makePayment(double amount);


}
