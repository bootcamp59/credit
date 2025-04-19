package com.bootcamp.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private Double amount;
    private AccountPaymentRequest origen;
    private AccountPaymentRequest destino;


}
