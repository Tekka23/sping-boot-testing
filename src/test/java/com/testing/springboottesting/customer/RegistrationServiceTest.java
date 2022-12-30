package com.testing.springboottesting.customer;

import com.testing.springboottesting.utils.PhoneNumberValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator phoneNumberValidator;
    @InjectMocks
    private RegistrationService underTests;

    @Test
    void isShouldSaveNewCustomer() {
        String phoneNumber = "89346662211";

        Customer customer = new Customer(UUID.randomUUID(), "Masha", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... No customer with phone_num will return
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        given(customerRepository
                .selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());


        underTests.registerNewCustomer(request);

        then(customerRepository).should().save(customerArgumentCaptor.capture());

        Customer customerCapturedValue = customerArgumentCaptor.getValue();
        assertThat(customerCapturedValue).isEqualTo(customer).usingRecursiveComparison();
    }

    @Test
    void isShouldSaveNewCustomerWhenIdIsNull() {
        String phoneNumber = "89346662211";
        //Given
        Customer customer = new Customer(null, "Masha", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);


        // ... No customer with phone_num will return
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        given(customerRepository
                .selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        underTests.registerNewCustomer(request);

        then(customerRepository).should().save(customerArgumentCaptor.capture());

        Customer customerCapturedValue = customerArgumentCaptor.getValue();
        assertThat(customerCapturedValue).isEqualTo(customer).usingRecursiveComparison();
        assertThat(customerArgumentCaptor.getValue().getId()).isNotNull();
    }

    @Test
    void isShouldItShouldNotSaveCustomerWhenCustomerExists() {
        String phoneNumber = "89346662211";
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer(uuid,"Masha", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        underTests.registerNewCustomer(request);

        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void isShouldThrowExceptionOnPhoneAlreadyTaken() {
        String phoneNumber = "89346662211";
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer(uuid, "Masha", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        Customer John = new Customer(UUID.randomUUID(), "John", phoneNumber);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(John));

        assertThatThrownBy(() -> underTests.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void isShouldThrowExceptionOnIfPhoneNumberNotValid() {
        String phoneNumber = "12243";
        Customer customer = new Customer(UUID.randomUUID(), "Masha", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        assertThatThrownBy(() -> underTests.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number {%s} not valid", phoneNumber));
    }
}