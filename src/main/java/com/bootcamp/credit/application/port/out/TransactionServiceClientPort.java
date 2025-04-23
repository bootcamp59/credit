package com.bootcamp.credit.application.port.out;


import com.bootcamp.credit.domain.dto.TransactionDto;
import reactor.core.publisher.Mono;

public interface TransactionServiceClientPort {

    Mono<Object> saveMovements(TransactionDto transactionDto);
}
