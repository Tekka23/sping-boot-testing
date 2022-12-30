package com.testing.springboottesting.customer;

import com.testing.springboottesting.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {
    private final CustomerRepository repository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public RegistrationService(CustomerRepository repository,
                               PhoneNumberValidator phoneNumberValidator) {
        this.repository = repository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request){
        String phoneNumber = request.customer().getPhoneNumber();
        Optional<Customer> customer = repository
                .selectCustomerByPhoneNumber(phoneNumber);


        if(!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException(String.format("Phone number {%s} not valid", phoneNumber));
        }

        if(customer.isPresent()){
            if(customer.get().equals(request.customer())){
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
        }
        if(request.customer().getId() == null){
            request.customer().setId(UUID.randomUUID());
        }
        repository.save(request.customer());
    }
    public Customer findById(UUID uuid){
        Optional<Customer> optionalCustomer = repository.findById(uuid);
        return optionalCustomer.orElse(null);
    }

}
