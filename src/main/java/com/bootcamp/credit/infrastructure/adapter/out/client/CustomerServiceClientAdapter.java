package com.bootcamp.credit.infrastructure.adapter.out.client;


import com.bootcamp.credit.application.port.out.CustomerServiceClientPort;
import com.bootcamp.credit.domain.dto.Customer;
import com.bootcamp.credit.domain.enums.CreditType;
import com.bootcamp.credit.infrastructure.config.CreditProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerServiceClientAdapter implements CustomerServiceClientPort {

    private final WebClient.Builder webClientBuilder;
    private final CreditProperties properties;


    @Override
    public Mono<Void> validateCustomerType(String document, CreditType creditType) {
        var url = properties.getMsCustomerApi() + "/" + document;

        return webClientBuilder.build()
            .get()
            .uri(url)
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
}
