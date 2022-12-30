package com.testing.springboottesting.payment.stripe;

import com.testing.springboottesting.payment.CardPaymentCharge;
import com.testing.springboottesting.payment.CardPaymentCharger;
import com.testing.springboottesting.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false")
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
        return new CardPaymentCharge(true);
    }
}
