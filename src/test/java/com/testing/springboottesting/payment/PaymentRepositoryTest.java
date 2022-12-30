package com.testing.springboottesting.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository underTest;

    @Test
    void isShouldInsertPayment() {

        //Given
        Payment payment = new Payment(1L,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "donation");
        //When
        underTest.save(payment);
        //Then
        assertThat(underTest.findById(1L))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByFieldRecursively(payment));

    }
}