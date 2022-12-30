package com.testing.springboottesting.payment;

public record CardPaymentCharge(boolean isCardCharged) {
    @Override
    public String toString() {
        return "CardPaymentCharge{" +
                "isCardCharged=" + isCardCharged +
                '}';
    }
}
