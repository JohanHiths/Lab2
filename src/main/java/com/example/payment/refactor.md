Extraherade Payment API till PaymentGateway

PaymentProcessor anropade paymentApi.charge direkt med statisk API-nyckel som det svårt att testa

så jag skapade PaymentGateway och flyttade API-anropet till StripeGateway

nu kan PaymentProcessor kan unit-testas genom att mocka PaymentGateway


Extraherade e-post till EmailService

statiskt EmailService.send ger sidoeffekter i tester och är svårt att mocka

så jag ersatte statiskt anrop med injicerat EmailService

Tog bort API_KEY från PaymentProcessor

och istället lät StripeGateway äga apiKey via konstruktor

renare PaymentProcessor + lättare att testa + bättre separation

PaymentGateway
Ansvarar för kommunikation med extern betaltjänst
och Kapslar in API-nyckel och externt API-anrop

PaymentRepository
Ansvarar för persistens av lyckade betalningar
och isolerar även databasanrop från affärslogik

EmailService
Ansvarar för att skicka e-post vid lyckad betalning
och Gör e-postutskick möjligt att mocka i tester

PaymentApiResponse
Representerar svaret från betal-API:t
Används av PaymentProcessor för att fatta affärsbeslut