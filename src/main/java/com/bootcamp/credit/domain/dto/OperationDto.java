package com.bootcamp.credit.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OperationDto {
    private double amount;
    private String productId;
    private String document;
    private String description;
}
