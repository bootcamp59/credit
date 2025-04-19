package com.bootcamp.credit.infrastructure.adapter.out.persistence;

import com.bootcamp.credit.application.port.out.CreditRepositoryPort;
import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.infrastructure.adapter.out.persistence.mapper.CreditEntityMapper;
import com.bootcamp.credit.infrastructure.adapter.out.persistence.repository.mongodb.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CreditAdapter implements CreditRepositoryPort {

    private final CreditRepository repository;


    @Override
    public Mono<Credit> create(Credit model) {
        return repository.save(CreditEntityMapper.toEntity(model))
            .map(CreditEntityMapper::toModel);
    }

    @Override
    public Mono<Credit> update(Credit model) {
        return repository.save(CreditEntityMapper.toEntity(model))
            .map(CreditEntityMapper::toModel);
    }

    @Override
    public Flux<Credit> findAll() {
        return repository.findAll()
            .map(CreditEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByProductoId(String productoId) {
        return repository.existsByProductoId(productoId);
    }

    @Override
    public Mono<Credit> findByProductId(String productoId) {
        return repository.findByProductoId(productoId)
                .map(CreditEntityMapper::toModel);
    }

    @Override
    public Flux<Credit> findByDocument(String document) {
        return repository.findByDocument(document)
            .map(CreditEntityMapper::toModel);
    }

    @Override
    public Mono<Long> countByDocumentAndType(String productoId, CreditType type) {
        return repository.countByDocumentAndType(productoId, type);
    }
}
