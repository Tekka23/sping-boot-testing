package com.testing.springboottesting.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository underTest;
    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        String phoneNumber = "000990";
        Customer customer = new Customer(UUID.randomUUID(), "Abel", phoneNumber);

        underTest.save(customer);

        assertThat(underTest.selectCustomerByPhoneNumber("000990")).isEqualTo(Optional.of(customer));
    }

    @Test
    void isShouldNotSelectCustomerIfPhoneNumberDoesNotExist() {
        String phoneNumber = "0000";

        assertThat(underTest.selectCustomerByPhoneNumber(phoneNumber)).isNotPresent();
    }

    @Test
    void isShouldSaveCustomer() {
       UUID uuid = UUID.randomUUID();
       Customer customer =  new Customer(uuid, "Abel", "0000");

       underTest.save(customer);

       Optional<Customer> testCustomer = underTest.findById(customer.getId());

       assertThat(testCustomer)
               .isPresent()
               .hasValueSatisfying(c -> assertThat(c).isEqualTo(customer).usingRecursiveComparison());
    }

    @Test
    void isShouldNotSaveCustomerWhenNameIsNull() {
        UUID uuid = UUID.randomUUID();
        Customer customer =  new Customer(uuid, null, "0000");

        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining(" not-null property references a null or transient value : com.testing.springboottesting.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void isShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        UUID uuid = UUID.randomUUID();
        Customer customer =  new Customer(uuid, "Abel", null);

        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining(" not-null property references a null or transient value : com.testing.springboottesting.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}