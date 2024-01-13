package com.codemountain.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
