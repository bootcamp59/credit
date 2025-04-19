package com.bootcamp.credit.infrastructure.adapter.out.persistence.entity;

import com.bootcamp.credit.domain.enums.CreditType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "credit")
public class CreditEntity {
    @Id
    private String id;

    @NotBlank(message = "Customer ID is required")
    private String document;

    @NotNull(message = "Credit type is required")
    private CreditType type;

    private String bankName;

    @NotBlank(message = "Credit number is required")
    private String productoId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double linea; //linea de credito/prestamo

    @NotNull(message = "Balance is required")
    @Min(value = 0, message = "Balance must be positive")
    private Double balance;

    @NotNull(message = "Interest rate is required")
    @Min(value = 0, message = "Interest rate must be positive")
    private Double interestRate;

    private LocalDate openingDate;
    private LocalDate dueDate;

    // Common fields for all credit types
    private Integer paymentDay; // Day of month for payments
    private Integer cutOfDay;
    private Integer remainingInstallments; // cuotas restantes

    // Fields specific to credit cards
    private Double creditUsageToPay; // credito usado por pagar
    private LocalDate paymentDate; // For credit cards
}
