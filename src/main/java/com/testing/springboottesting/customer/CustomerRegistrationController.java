package com.testing.springboottesting.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer-registration")
public class CustomerRegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public CustomerRegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PutMapping
    public void registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request){
        log.info("Register method init");
        registrationService.registerNewCustomer(request);
    }
    @GetMapping("/{customerId}")
    public Customer getCustomer (@PathVariable UUID  customerId){
        return registrationService.findById(customerId);
    }

}
