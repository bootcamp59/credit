package com.bootcamp.credit.domain.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBankDto {
    private String productoId;
    private String document;
    private String banco;
    private String type;



}
