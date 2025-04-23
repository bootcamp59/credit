package com.bootcamp.credit.application.port.out;

import com.bootcamp.credit.domain.enums.CreditType;
import reactor.core.publisher.Mono;

public interface CustomerServiceClientPort {

    Mono<Void> validateCustomerType(String document, CreditType creditType);
}
