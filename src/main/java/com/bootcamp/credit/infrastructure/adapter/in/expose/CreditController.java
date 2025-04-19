package com.bootcamp.credit.infrastructure.adapter.in.expose;

import com.bootcamp.credit.application.port.in.CreditUseCase;
import com.bootcamp.credit.domain.dto.OperationDto;
import com.bootcamp.credit.domain.model.Credit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditUseCase useCase;

    @GetMapping
    public Flux<Credit> getAllCredits() {
        return useCase.findAll();
    }

    @GetMapping("/customer/{docNumber}")
    public Flux<Credit> getCreditsByCustomerId(@PathVariable String docNumber) {
        return useCase.findByDocument(docNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Credit> createCredit(@RequestBody Credit credit) {
        return useCase.create(credit);
    }

    @PostMapping("/payment")
    public Mono<ResponseEntity<OperationDto>> payment(@RequestBody OperationDto request) {
        return useCase.payment(request)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/consumption")
    public Mono<ResponseEntity<OperationDto>> chargeConsumption(@RequestBody OperationDto request) {
        return useCase.chargeConsumption(request)
                .map(resp -> ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resp));
    }
}
