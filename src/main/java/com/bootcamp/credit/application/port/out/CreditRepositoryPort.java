package com.bootcamp.credit.application.port.out;

import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.domain.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepositoryPort {
    Mono<Credit> create(Credit model);
    Mono<Credit> update(Credit model);
    Flux<Credit> findAll();
    Mono<Boolean> existsByProductoId(String productoId);
    Mono<Credit> findByProductId(String productoId);
    Flux<Credit> findByDocument(String document);
    Mono<Long> countByDocumentAndType(String productoId, CreditType type);

}
