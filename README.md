# Lab2
Lab2 - Testing

Refaktoriseringsbeslut

Först gjorde jag EmailService, PaymentApiResponse och PaymentService

EmailService ansvarar för att skicka e-post vid lyckad betalning.

PaymentApiResponse ansvarar för att representera svaret från betal-APIet.

PaymentService ansvarar för beslut och flöde. Använder paymentgateway för att genomföra betalningar och paymentrepository för att spara betalningar, emailservice för att skicka bekräftelse vid lyckad betalning. Ingen teknisk implmentation api-nycklar, SQL eller e-postdetaljer.


