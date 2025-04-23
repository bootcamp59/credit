package com.bootcamp.credit.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "credit")
@Getter
@Setter
public class CreditProperties {

    private String msTransactionApi;
    private String msCustomerApi;
}
