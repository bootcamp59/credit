package com.bootcamp.credit.domain.model;

import com.bootcamp.credit.domain.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Credit {

    private String id;
    private String document;
    private CreditType type;
    private String bankName;
    private String productoId;
    private Double linea; //linea de credito/prestamo
    private Double balance;
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

    public void consumption(Double monto){
        if(monto < 0) {
            throw new IllegalArgumentException("consumption must be positive");
        }
        if((creditUsageToPay + monto) > linea){
            throw new IllegalArgumentException("consumption has exceeded the limit");
        }
        this.balance = this.getBalance() - monto;
        this.creditUsageToPay = this.getCreditUsageToPay() + monto;
    }
}
