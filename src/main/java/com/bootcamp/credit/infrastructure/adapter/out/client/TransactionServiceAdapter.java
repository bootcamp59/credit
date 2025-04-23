package com.bootcamp.credit.infrastructure.adapter.out.client;

import com.bootcamp.credit.application.port.out.TransactionServiceClientPort;
import com.bootcamp.credit.domain.dto.TransactionDto;
import com.bootcamp.credit.infrastructure.config.CreditProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceAdapter implements TransactionServiceClientPort {

    private final WebClient.Builder webClientBuilder;
    private final CreditProperties properties;

    @Override
    public Mono<Object> saveMovements(TransactionDto transactionDto) {
        var url = properties.getMsTransactionApi();

        return webClientBuilder.build()
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap( e -> new RuntimeException("error al enviar la transaccion"))
                .doOnError(o -> log.error("Error al enviar la transaccion"));
    }
}
