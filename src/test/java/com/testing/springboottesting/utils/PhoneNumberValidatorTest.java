package com.testing.springboottesting.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;
    @BeforeEach
    void setUp(){
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({"+79307633861,true", "89307633861,true", "12345,false", "10000sd011241, false"})
    void isShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        boolean isValid = underTest.test(phoneNumber);
        assertThat(isValid).isEqualTo(expected);
    }


}
