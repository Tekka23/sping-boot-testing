package com.testing.springboottesting.payment;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long paymentId;
    
    private UUID customerId;

    private BigDecimal amount;

    private Currency currency;

    private String source;

    private String description;

    public Payment(Long paymentId,
                   UUID customerId,
                   BigDecimal amount,
                   Currency currency,
                   String source,
                   String description) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.source = source;
        this.description = description;
    }

    public Payment() {
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerUUID) {
        this.customerId = customerUUID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return paymentId.equals(payment.paymentId) && customerId.equals(payment.customerId) && amount.equals(payment.amount) && currency == payment.currency && source.equals(payment.source) && description.equals(payment.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, customerId, amount, currency, source, description);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", customerUUID=" + customerId +
                ", amount=" + amount +
                ", currency=" + currency +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
