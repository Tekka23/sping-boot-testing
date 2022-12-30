package com.testing.springboottesting.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testing.springboottesting.customer.Customer;
import com.testing.springboottesting.customer.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void isShouldCreatePaymentSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "89447647640");

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        ResultActions customerRegResultAction = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        long paymentId = 1L;

        Payment payment = new Payment(paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,"0x0x0x0",
                "donation");

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        customerRegResultAction.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(
                        (p) ->  assertThat(p).isEqualTo(payment).usingRecursiveComparison()
                );

    }

    private <T> String objectToJson(T t) {
        try{

            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(t));
            return objectMapper.writeValueAsString(t);
        }
        catch (JsonProcessingException e){
            fail("Error trying to parse object" + e.getMessage());
            return null;
        }
    }
}
