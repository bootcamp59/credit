package com.bootcamp.credit.dto;

import com.bootcamp.credit.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {


    private TransactionAccountBankRequest origen;
    private TransactionAccountBankRequest destino;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    private String description;
    private LocalDateTime transactionDate;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private Double transactionCommission;
}
