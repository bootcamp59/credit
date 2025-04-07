package com.bootcamp.credit.model;

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
public class Credit {
    @Id
    private String id;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Credit type is required")
    private CreditType type;

    @NotBlank(message = "Credit number is required")
    private String creditNumber;

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
    private Integer remainingInstallments; // cuotas restantes

    // Fields specific to credit cards
    private Double creditLimit;
    private Double creditUsageToPay; // credito usado por pagar
    private LocalDate cutDate; // For credit cards
    private LocalDate paymentDate; // For credit cards

    public enum CreditType {
        PERSONAL, EMPRESARIAL, CREDIT_CARD
    }
}
