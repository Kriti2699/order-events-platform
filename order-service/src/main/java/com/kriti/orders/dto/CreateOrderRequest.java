package com.kriti.orders.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank @Email String customerEmail,
        @NotNull @DecimalMin("0.01") BigDecimal amount) {
}
