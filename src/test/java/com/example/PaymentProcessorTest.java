package com.example;

import com.example.payment.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

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
    @DisplayName("Ska gå vidare med betalning när betalningen lyckas")
    void shouldProcessPaymentWhenGatewaySucceeds() throws SQLException {
        double amount = 199.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(true);

        boolean result = paymentProcessor.processPayment(amount, "user@example.com");

        assertThat(result).isTrue();
        verify(paymentRepository).saveSuccessfulPayment(amount);
        verify(emailService).sendPaymentConfirmation("user@example.com", amount);
    }

    @Test
    @DisplayName("Ska inte gå vidare med betalning när betalningen misslyckas")
    void shouldNotProcessPaymentWhenGatewayFails() throws SQLException {
        double amount = 49.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(false);

        boolean result = paymentProcessor.processPayment(amount, "user@example.com");

        assertThat(result).isFalse();
        verify(paymentRepository, never()).saveSuccessfulPayment((amount));
        verify(emailService, never()).sendPaymentConfirmation("user@example.com", amount);
    }

    @Test
    @DisplayName("Ska kunna hantera krasher i databasen efter lyckad betalning ordentligt")
    void makeSureNocrashsesInDatabaseAfterPayment() throws SQLException {
        double amount = 199.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(true);
        doThrow(new SQLException("Database offline")).when(paymentRepository).saveSuccessfulPayment(anyDouble());

        assertThatThrownBy(() -> paymentProcessor.processPayment(amount, "user@example.com")).isInstanceOf(SQLException.class);

        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());
    }

    //Först en vanligt arrange
    //Sen rigga mejlfelet
    //Fånga Krashen
    //Kontrollera att pengarna sparas
    //Och att mejlet var orsaken
    @Test
    @DisplayName("Kunden har betalat och vi ska spara pengar i data basen men mejlet skickas inte")
    void paymentIsSuccessfulButEmailIsNotSent() throws SQLException {
        double amount = 100.0;

        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(true);

        doThrow(new RuntimeException("Email server down"))
                .when(emailService).sendPaymentConfirmation(anyString(), anyDouble());

        assertThatThrownBy(() -> paymentProcessor.processPayment(amount, "user@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email server down");

        verify(paymentRepository).saveSuccessfulPayment(amount);
        verify(emailService).sendPaymentConfirmation(anyString(), anyDouble());

    }
//    @Test
//    @DisplayName("Betalning med negativt värde ska inte fungera")
//    void negativePaymentShouldNotBeProcessed() throws SQLException {
//
//        double amount = -100.0;
//
//        when(paymentGateway.charge(BigDecimal.valueOf(amount))).thenReturn(paymentApiResponse);
//        when(paymentApiResponse.isSuccess()).thenReturn(false);
//
//        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentGateway, paymentRepository, emailService);
//        boolean result = paymentProcessor.processPayment(amount);
//
//        assertThat(result).isFalse();
//        verify(paymentGateway).charge(BigDecimal.valueOf(amount));
//        verify(paymentRepository, never()).saveSuccessfulPayment(anyDouble());
//        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());
//
//    }


    //Testar att "invalid amount" inte så fungera
    //Verifierar att vi aldrig ska ens dra pengar överhuvudtaget
    @ParameterizedTest
    @ValueSource(doubles = {-100.0, 0.0, -0.01})
    @DisplayName("Ogiltila belopp ska returna false")
    void invalidAmountShouldReturnFalse(double amount) throws SQLException {
        boolean result = paymentProcessor.processPayment(amount, "user@example.com");

        assertThat(result).isFalse();

        verify(paymentGateway, never()).charge(any());
        verify(paymentRepository, never()).saveSuccessfulPayment(anyDouble());
        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());

    }

    @Test
    @DisplayName("Ska kunna betala med Stripe")
    void shouldChargePaymentSuccessfullyThroughStripeGateway() {
        double amount = 100.0;

        PaymentGateway stripeGateway = new StripeGateway("sk_test_123456789");

        PaymentApiResponse response = stripeGateway.charge(BigDecimal.valueOf(amount));
        System.out.println("Betalning " + response.isSuccess());

        assertThat(response.isSuccess()).isTrue();

    }

    @Test
    @DisplayName("Mottagare saknas eller ogiltlig email")
    void userMissingOrHasInvalidEmail() {
        double amount = 100.0;

        when(paymentGateway.charge(BigDecimal.valueOf(100.0))).thenReturn(paymentApiResponse);
        when(paymentApiResponse.isSuccess()).thenReturn(true);

        doThrow(new RuntimeException("Ogiltig användare eller emailadress"))
                .when(emailService).sendPaymentConfirmation(anyString(), anyDouble());


        assertThatThrownBy(() -> paymentProcessor.processPayment(100.0, "asdasd@gmail.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ogiltig användare eller emailadress");
    }
    //Anropar först ett korrekt belopp sen en felaktig emailadress
    // och vi förväntar oss att metoden fångar och returnar false
    // sen kontrollerar vi att det blev false
    // och sedan bekräftar vi att processen inte drog några pengar
    @ParameterizedTest
    @NullSource // ser till att ett nullvärde är med i testet!
    @ValueSource(strings = {"", "  "})
    @DisplayName("Hantera användarnamn med tom sträng eller null ordentligt")
    void handleUsernameWithEmptyStringorNullCorrectly(String invalidEmail) throws SQLException {

        boolean result = paymentProcessor.processPayment(100.0, invalidEmail);

        assertThat(result).isFalse();

        verify(paymentGateway, never()).charge(any());
        verify(paymentRepository, never()).saveSuccessfulPayment(anyDouble());
        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());

    }

    @Test
    @DisplayName("Neka för stor betalning")
    void denyPaymentForTooLargeAmounts() throws SQLException {
        double amount = Double.MAX_VALUE;

        boolean result = paymentProcessor.processPayment(amount, "user@example.com");

        assertThat(result).isFalse();
        verify(paymentRepository, never()).saveSuccessfulPayment((amount));
        verify(emailService, never()).sendPaymentConfirmation("user@example.com", amount);

    }

}