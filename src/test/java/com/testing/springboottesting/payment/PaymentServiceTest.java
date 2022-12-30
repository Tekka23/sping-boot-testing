package com.testing.springboottesting.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testing.springboottesting.customer.Customer;
import com.testing.springboottesting.customer.CustomerRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger paymentCharger;
    @InjectMocks
    private PaymentService underTests;

    @Test
    @Tag(value = "fast")
    void isShouldChargeCard() {
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        PaymentRequest request = new PaymentRequest(new Payment(null,
                null,
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "donation"));

        given(paymentCharger.chargeCard(
                request.payment().getSource(),
                request.payment().getAmount(),
                request.payment().getCurrency(),
                request.payment().getDescription()))
                .willReturn(new CardPaymentCharge(true));

        underTests.chargeCard(customerId, request);

        then(paymentRepository).should().save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue()).isEqualTo(request.payment()).usingRecursiveComparison();
        assertThat(paymentCaptor.getValue().getCustomerId()).isEqualTo(customerId);
        then(paymentRepository).shouldHaveNoMoreInteractions();
        then(paymentCharger).shouldHaveNoMoreInteractions();

    }

    @Test
    @Tag(value = "fast")
    void isShouldThrowExceptionWhenUserNotFound() throws JsonProcessingException {
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(new Payment(null,
                null,
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "donation"));

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTests.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Can not find user with id {%s}", customerId));

        then(paymentRepository).should(never()).save(any());
        then(paymentRepository).shouldHaveNoInteractions();
        then(paymentCharger).shouldHaveNoInteractions();
    }

    @Test
    void isShouldThrowExceptionWhenCurrencyDoesNotSupported() {
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(new Payment(null,
                null,
                new BigDecimal("10.00"),
                Currency.valueOf("EUR"),
                "card123",
                "donation"));
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        assertThatThrownBy(() -> underTests.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Can not use this currency [%s]", request.payment().getCurrency()));
        then(paymentRepository).should(never()).save(any());
        then(paymentRepository).shouldHaveNoInteractions();
        then(paymentCharger).shouldHaveNoInteractions();

    }

    @Test
    void isShouldThrowExceptionWhenCardNotCharged() {
        UUID customerId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(new Payment(null,
                null,
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "donation"));

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        given(paymentCharger.chargeCard(
                request.payment().getSource(),
                request.payment().getAmount(),
                request.payment().getCurrency(),
                request.payment().getDescription()))
                .willReturn(new CardPaymentCharge(false));

        assertThatThrownBy(() -> underTests.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not charged for customer with id {%s}", customerId));
        then(paymentRepository).should(never()).save(any());
        then(paymentRepository).shouldHaveNoInteractions();
        then(paymentCharger).shouldHaveNoMoreInteractions();
    }
}