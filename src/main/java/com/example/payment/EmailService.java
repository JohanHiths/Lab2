package com.example.payment;

import com.example.Booking;

import java.sql.PreparedStatement;

public interface EmailService {

    void sendPaymentConfirmation(String email, double amount);

}
