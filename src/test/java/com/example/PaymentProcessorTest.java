package com.example;

import com.example.payment.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @InjectMocks
    PaymentProcessor paymentProcessor;
    @Mock
    EmailService emailService;
    @Mock
    PaymentGateway paymentGateway;
    @Mock
    PaymentRepository paymentRepository;


    @Test
    @DisplayName("Kollar om betalningen lyckades")
    void processSuccessfulPayment() throws SQLException {

    }

    @Test
    @DisplayName("Kollar om betalningen misslyckades")
    void processFailedPayment() throws SQLException {

    }

    @Test
    @DisplayName("Tester EmailServie")
    void processSuccessfulPaymentTest() throws SQLException {
        PaymentGateway paymentGateway = mock(PaymentGateway.class);
        EmailService emailService = mock(EmailService.class);

        when(paymentGateway);

        emailService.sendPaymentConfirmation("asdasd@hotmail.com", 100.0);


        verify(emailService).sendPaymentConfirmation("asdasd@hotmail.com", 100.0);

    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 100.0, 999.99})
    void processPayment_success_savesAndSendsEmail(double amount) throws SQLException {
         //Act
        boolean result = paymentProcessor.processPayment(amount);

        // Assert
        Assertions.assertThat(result).isTrue();

       // verify
        verify(paymentGateway).charge(amount);
        verify(paymentRepository).saveSuccessfulPayment(amount);
        verify(emailService).sendPaymentConfirmation("user@example.com", amount);

        // extra
        verifyNoMoreInteractions(paymentGateway, paymentRepository, emailService);
    }




}