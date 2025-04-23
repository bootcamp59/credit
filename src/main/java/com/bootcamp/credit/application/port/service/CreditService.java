package com.bootcamp.credit.application.port.service;

import com.bootcamp.credit.application.port.in.CreditUseCase;
import com.bootcamp.credit.application.port.out.CreditRepositoryPort;
import com.bootcamp.credit.application.port.out.CustomerServiceClientPort;
import com.bootcamp.credit.application.port.out.TransactionServiceClientPort;
import com.bootcamp.credit.domain.dto.AccountBankDto;
import com.bootcamp.credit.domain.dto.OperationDto;
import com.bootcamp.credit.domain.dto.TransactionDto;
import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.domain.enums.TransactionType;
import com.bootcamp.credit.domain.model.Credit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditService implements CreditUseCase {

    private final CreditRepositoryPort port;
    private final TransactionServiceClientPort transactionServiceClientPort;
    private final CustomerServiceClientPort customerServiceClientPort;

    @Override
    public Flux<Credit> findAll() {
        return port.findAll();
    }

    @Override
    public Flux<Credit> findByDocument(String document) {
        return port.findByDocument(document);
    }

    @Override
    public Mono<Credit> create(Credit model) {
        return customerServiceClientPort.validateCustomerType(model.getDocument(), model.getType())
            .then(validateCreditLimits(model.getDocument(), model.getType()))
            .then(port.existsByProductoId(model.getProductoId()))
            .flatMap(exists -> {
                return exists
                    ? Mono.error(new IllegalArgumentException("Credit number already exists"))
                    : saveNewCredit(model); });
    }

    @Override
    public Mono<OperationDto> payment(OperationDto operationDto) {
        return port.findByProductId(operationDto.getProductId())
            .flatMap(credit -> {
                if(credit.getType() == CreditType.CREDIT_CARD){
                    credit.setCreditUsageToPay(credit.getCreditUsageToPay() - operationDto.getAmount());
                    credit.setBalance(credit.getBalance() + operationDto.getAmount());
                } else {
                    credit.setBalance( credit.getBalance() - operationDto.getAmount());
                }
                return port.update(credit);
            })
            .flatMap(res -> {
                var request = buildTransactionRequest(operationDto, res, TransactionType.PAYMENT);
                return transactionServiceClientPort.saveMovements(request);
            })
            .thenReturn(operationDto)
                .onErrorResume( error -> {
                    System.out.println("Error during payment: " + error.getMessage());
                    return Mono.error(new RuntimeException("Error while processing payment: " + error.getMessage()));
                });

    }

    @Override
    public Mono<OperationDto> chargeConsumption(OperationDto operationDto) {
        return port.findByProductId(operationDto.getProductId())
                .flatMap(credit -> {
                    credit.consumption(operationDto.getAmount());
                    return port.update(credit);
                })
                .flatMap(res -> {
                    var request = buildTransactionRequest(operationDto, res, TransactionType.CONSUMPTION);
                    return transactionServiceClientPort.saveMovements(request);
                })
                .thenReturn(operationDto)
                .onErrorResume( error -> {
                    System.out.println("Error during payment: " + error.getMessage());
                    return Mono.error(new RuntimeException("Error while processing payment: " + error.getMessage()));
                });
    }

    @Override
    public Flux<Credit> hasDebt(String document) {
        return port.findByDocument(document)
            .map( credit -> {
                var today = LocalDate.now();
                int paymentDay = credit.getPaymentDay();
                LocalDate fechaLimite = today.withDayOfMonth(Math.min(paymentDay, today.lengthOfMonth()));
                //if(credit.getType() == CreditType.CREDIT_CARD){
                    if (today.isAfter(fechaLimite)) {
                        if(credit.getType() == CreditType.CREDIT_CARD){
                            if(credit.getCreditUsageToPay() > 0){
                                return credit;
                            }
                        } else {
                            if(credit.getBalance() > 0){
                                return credit;
                            }
                        }

                    }
                //}
                return new Credit();
            })
            .filter(c -> c.getId() != null);
    }

    private Mono<Void> validateCreditLimits(String customerId, CreditType creditType) {
        if (creditType == CreditType.PERSONAL) {
            return port.countByDocumentAndType(customerId, creditType)
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException(
                            "Personal customers can only have one personal loan"));
                    }
                    return Mono.empty();
                });
        }
        return Mono.empty();
    }

    private Mono<Credit> saveNewCredit(Credit credit) {
        credit.setOpeningDate(LocalDate.now());
        credit.setBalance(credit.getLinea());

        if (credit.getType() == CreditType.CREDIT_CARD) {
            credit.setCreditUsageToPay(0.0);
        }

        return port.create(credit);
    }

    private TransactionDto buildTransactionRequest(OperationDto dto, Credit credit, TransactionType transactionType){
        return TransactionDto.builder()
            .amount(dto.getAmount())
            .type(transactionType)
            .description(dto.getDescription())
            .origen(AccountBankDto.builder()
                .document(dto.getDocument())
                .build())
            .destino(AccountBankDto.builder()
                .productoId(dto.getProductId())
                .document(credit.getDocument())
                .banco(credit.getBankName())
                .type(credit.getType().name())
                .build())
            .build();
    }
}
