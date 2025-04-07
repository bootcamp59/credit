package com.bootcamp.credit.dto;

import com.bootcamp.credit.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditRequestDTO {
    private double monto;
    private TransactionType transactionType;


}
