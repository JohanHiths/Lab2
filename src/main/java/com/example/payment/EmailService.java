package com.example.payment;

import java.util.function.IntPredicate;

//
public interface EmailService {

    void sendPaymentConfirmation(String email, double amount);


}
