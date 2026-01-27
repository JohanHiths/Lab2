package com.example.payment;

//
public interface EmailService {

    boolean sendPaymentConfirmation(String email, double amount);


}
