package com.bootcamp.credit.domain.dto;


import com.bootcamp.credit.domain.enums.TransactionType;
import jakarta.validation.constraints.Min;
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
public class TransactionDto {

    private String id;
    private AccountBankDto origen;
    private AccountBankDto destino;

    private TransactionType type;
    private Double amount;

    private String description;
    private LocalDateTime transactionDate;

    private Double transactionCommission;
}
