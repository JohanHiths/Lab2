package com.example;

import com.example.payment.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentProcessorTest {
    private PaymentProcessor paymentProcessor;
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final PaymentService paymentService = mock(PaymentService.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final EmailService emailService = mock(EmailService.class);


    @Test
    @DisplayName("Kollar om betalningen lyckades")
    void processSuccessfulPayment() throws SQLException {


    }
}