package com.bootcamp.credit.infrastructure.adapter.out.persistence.mapper;

import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.infrastructure.adapter.out.persistence.entity.CreditEntity;

public class CreditEntityMapper {

    public static Credit toModel(CreditEntity entity){
        return Credit.builder()
            .id(entity.getId())
            .document(entity.getDocument())
            .type(entity.getType())
            .bankName(entity.getBankName())
            .productoId(entity.getProductoId())
            .linea(entity.getLinea())
            .balance(entity.getBalance())
            .interestRate(entity.getInterestRate())
            .dueDate(entity.getDueDate())
            .openingDate(entity.getOpeningDate())
            .paymentDate(entity.getPaymentDate())
            .remainingInstallments(entity.getRemainingInstallments())
            .creditUsageToPay(entity.getCreditUsageToPay())
            .paymentDate(entity.getPaymentDate())
            .build();
    }

    public static CreditEntity toEntity(Credit model){
        return CreditEntity.builder()
            .id(model.getId())
            .document(model.getDocument())
            .type(model.getType())
            .bankName(model.getBankName())
            .productoId(model.getProductoId())
            .linea(model.getLinea())
            .balance(model.getBalance())
            .interestRate(model.getInterestRate())
            .dueDate(model.getDueDate())
            .openingDate(model.getOpeningDate())
            .paymentDate(model.getPaymentDate())
            .remainingInstallments(model.getRemainingInstallments())
            .creditUsageToPay(model.getCreditUsageToPay())
            .paymentDate(model.getPaymentDate())
            .build();
    }
}
