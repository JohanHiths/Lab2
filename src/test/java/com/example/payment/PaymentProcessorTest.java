package com.example.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PaymentApiResponse paymentApiResponse;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Test
    @DisplayName("Returns true and persists/sends confirmation on successful charge")
    void shouldProcessPaymentWhenGatewaySucceeds() throws SQLException {
        double amount = 199.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(true);

        boolean result = paymentProcessor.processPayment(amount);

        assertThat(result).isTrue();
        verify(paymentRepository).saveSuccessfulPayment(amount);
        verify(emailService).sendPaymentConfirmation("user@example.com", amount);
    }

    @Test
    @DisplayName("Returns false and does not persist/send confirmation on failed charge")
    void shouldNotProcessPaymentWhenGatewayFails() throws SQLException {
        double amount = 49.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(false);

        boolean result = paymentProcessor.processPayment(amount);

        assertThat(result).isFalse();
        verify(paymentRepository, never()).saveSuccessfulPayment(amount);
        verify(emailService, never()).sendPaymentConfirmation("user@example.com", amount);
    }
}
