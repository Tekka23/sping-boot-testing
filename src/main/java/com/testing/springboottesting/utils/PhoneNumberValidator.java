package com.testing.springboottesting.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class PhoneNumberValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        return s.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
    }
}
