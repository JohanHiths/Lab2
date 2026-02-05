package com.example.payment;


import java.sql.SQLException;

///
public interface PaymentRepository {

    void saveSuccessfulPayment(double amount) throws SQLException;


}
