package com.testing.springboottesting.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerRegistrationRequest(Customer customer) {
    public CustomerRegistrationRequest(
            @JsonProperty("customer") Customer customer) {
        this.customer = customer;
    }
    @Override
    public Customer customer() {
        return customer;
    }

    @Override
    public String toString() {
        return "CustomerRegistrationRequest{" +
                "customer=" + customer +
                '}';
    }
}
