package com.bootcamp.credit.repository;

import com.bootcamp.credit.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCustomerId(String customerId);
    Flux<Credit> findByCustomerIdAndType(String customerId, Credit.CreditType type);
    Mono<Credit> findByCreditNumber(String creditNumber);
    Mono<Boolean> existsByCreditNumber(String creditNumber);
    Mono<Long> countByCustomerIdAndType(String customerId, Credit.CreditType type);
}
