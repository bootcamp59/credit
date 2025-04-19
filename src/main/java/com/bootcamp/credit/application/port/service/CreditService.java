package com.bootcamp.credit.application.port.service;

import com.bootcamp.credit.application.port.in.CreditUseCase;
import com.bootcamp.credit.application.port.out.CreditRepositoryPort;
import com.bootcamp.credit.domain.dto.AccountBankDto;
import com.bootcamp.credit.domain.dto.OperationDto;
import com.bootcamp.credit.domain.dto.TransactionDto;
import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.domain.enums.TransactionType;
import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditService implements CreditUseCase {

    private final CreditRepositoryPort port;
    private final WebClient.Builder webClientBuilder;

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
        return validateCustomerType(model.getDocument(), model.getType())
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
                return saveMovements(request);
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
                    return saveMovements(request);
                })
                .thenReturn(operationDto)
                .onErrorResume( error -> {
                    System.out.println("Error during payment: " + error.getMessage());
                    return Mono.error(new RuntimeException("Error while processing payment: " + error.getMessage()));
                });
    }


    private Mono<Void> validateCustomerType(String document, CreditType creditType) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v1/customer/customers/{docNumber}", document)
                .retrieve()
                .bodyToMono(Customer.class)
                .flatMap(customer -> {
                    if (creditType == CreditType.PERSONAL &&
                            customer.getType() != Customer.CustomerType.PERSONAL) {
                        return Mono.error(new IllegalArgumentException(
                                "Personal loans are only for personal customers"));
                    }
                    return Mono.empty();
                });
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

    private Mono<Object> saveMovements(TransactionDto transactionDto){
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8084/api/v1/transaction")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap( e -> new RuntimeException("error al enviar la transaccion"))
                .doOnError(o -> log.error("Error al enviar la transaccion"));
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
