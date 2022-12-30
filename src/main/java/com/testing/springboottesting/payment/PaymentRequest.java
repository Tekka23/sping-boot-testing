package com.testing.springboottesting.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentRequest(Payment payment) {
    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "payment=" + payment +
                '}';
    }
}
