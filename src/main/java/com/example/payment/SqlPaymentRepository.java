package com.example.payment;

public final class SqlPaymentRepository implements PaymentRepository {
    private final DatabaseConnection databaseConnection;

    public SqlPaymentRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void saveSuccess(double amount) {
        databaseConnection.executeUpdate(
                "INSERT INTO payments (amount, status) VALUES (" + amount + ", 'SUCCESS')"
        );
    }

    @Override
    public void saveSuccessfulPayment(double amount, String status) {

    }
}