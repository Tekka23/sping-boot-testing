package com.testing.springboottesting.payment;

import com.testing.springboottesting.customer.Customer;
import com.testing.springboottesting.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PaymentService {
    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger charger;

    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository, CardPaymentCharger charger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.charger = charger;
    }


    void chargeCard(UUID uuid, PaymentRequest request){
        Optional<Customer> customer = customerRepository.findById(uuid);
        boolean customerIsEmpty = customer.isEmpty();
        if(customerIsEmpty) throw new IllegalStateException(String.format("Can not find user with id {%s}", uuid));

        if(ACCEPTED_CURRENCIES.stream().noneMatch(c -> c.equals(request.payment().getCurrency())))
            throw new IllegalStateException(String.format("Can not use this currency [%s]", request.payment().getCurrency()));

        CardPaymentCharge cardPaymentCharge = charger.chargeCard(request.payment().getSource(),
                request.payment().getAmount(),
                request.payment().getCurrency(),
                request.payment().getDescription());

        if(!cardPaymentCharge.isCardCharged())
            throw new IllegalStateException(String.format("Card not charged for customer with id {%s}", uuid));

        request.payment().setCustomerId(uuid);
        paymentRepository.save(request.payment());
        }

}
