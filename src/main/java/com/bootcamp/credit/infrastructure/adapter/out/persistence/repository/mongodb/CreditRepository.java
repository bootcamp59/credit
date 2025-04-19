package com.bootcamp.credit.infrastructure.adapter.out.persistence.repository.mongodb;

import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.infrastructure.adapter.out.persistence.entity.CreditEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepository extends ReactiveMongoRepository<CreditEntity, String> {
    Flux<CreditEntity> findByDocument(String document);
    Mono<CreditEntity> findByProductoId(String productId);
    Mono<Boolean> existsByProductoId(String creditNumber);
    Mono<Long> countByDocumentAndType(String customerId, CreditType type);
}
