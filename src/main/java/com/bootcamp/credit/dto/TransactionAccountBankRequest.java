package com.bootcamp.credit.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionAccountBankRequest {

    private String productId;
    private String customerId;
    private String bankName;
    private String productType;

}
