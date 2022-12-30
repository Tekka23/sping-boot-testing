package com.testing.springboottesting.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.testing.springboottesting.payment.CardPaymentCharge;
import com.testing.springboottesting.payment.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {
    @Mock
    private StripeApi stripeApi;
    @InjectMocks
    private StripeService underTest;
    @Test
    void isShouldChargeCard() throws StripeException {

        //Given
        Currency currency = Currency.USD;
        BigDecimal amount = new BigDecimal("10.00");
        String source = "0x0x0";
        String description = "donation";
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);
        //When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(source, amount, currency, description);
        //Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
        then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

        Map<String, Object> mapValue = mapArgumentCaptor.getValue();

        assertThat(mapValue.keySet()).hasSize(4);
        assertThat(mapValue.get("amount")).isEqualTo(amount);
        assertThat(mapValue.get("currency")).isEqualTo(currency);
        assertThat(mapValue.get("source")).isEqualTo(source);
        assertThat(mapValue.get("description")).isEqualTo(description);

        RequestOptions argumentCaptorValue = optionsArgumentCaptor.getValue();

        assertThat(argumentCaptorValue).isNotNull();
        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardCharged()).isTrue();
    }

    @Test
    void isShouldThrowExceptionIfUnableToCharge() throws StripeException {
        Currency currency = Currency.USD;
        BigDecimal amount = new BigDecimal("10.00");
        String source = "0x0x0";
        String description = "donation";
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willThrow(mock(StripeException.class));
        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(source, amount, currency, description))
                .isInstanceOf(IllegalStateException.class);
        then(stripeApi).shouldHaveNoMoreInteractions();
    }
}