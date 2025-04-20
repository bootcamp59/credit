package com.bootcamp.credit.application.port.in;

import com.bootcamp.credit.domain.dto.OperationDto;
import com.bootcamp.credit.domain.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditUseCase {
    Flux<Credit> findAll();
    Flux<Credit> findByDocument(String document);
    Mono<Credit> create(Credit model);
    Mono<OperationDto> payment(OperationDto operationDto);
    Mono<OperationDto> chargeConsumption(OperationDto operationDto);
    Flux<Credit> hasDebt(String document);

}
