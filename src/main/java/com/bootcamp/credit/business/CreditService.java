package com.bootcamp.credit.business;

import com.bootcamp.credit.model.Credit;
import com.bootcamp.credit.model.Customer;
import com.bootcamp.credit.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@RequiredArgsConstructor
@Service
public class CreditService {

    private final CreditRepository creditRepository;
    private final WebClient.Builder webClientBuilder;

    public Flux<Credit> findAll() {
        return creditRepository.findAll();
    }

    public Mono<Credit> findById(String id) {
        return creditRepository.findById(id);
    }

    public Flux<Credit> findByCustomerId(String customerId) {
        return creditRepository.findByCustomerId(customerId);
    }

    public Mono<Credit> create(Credit credit) {
        return validateCustomerType(credit.getCustomerId(), credit.getType())
                .then(validateCreditLimits(credit.getCustomerId(), credit.getType()))
                .then(creditRepository.existsByCreditNumber(credit.getCreditNumber()))
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalArgumentException("Credit number already exists"))
                        : saveNewCredit(credit));
    }

    private Mono<Void> validateCustomerType(String customerId, Credit.CreditType creditType) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v1/customer/{id}", customerId)
                .retrieve()
                .bodyToMono(Customer.class)
                .flatMap(customer -> {
                    if (creditType == Credit.CreditType.PERSONAL &&
                            customer.getType() != Customer.CustomerType.PERSONAL) {
                        return Mono.error(new IllegalArgumentException(
                                "Personal loans are only for personal customers"));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateCreditLimits(String customerId, Credit.CreditType creditType) {
        if (creditType == Credit.CreditType.PERSONAL) {
            return creditRepository.countByCustomerIdAndType(customerId, creditType)
                    .flatMap(count -> {
                        if (count > 0) {
                            return Mono.error(new IllegalArgumentException(
                                    "Personal customers can only have one personal loan"));
                        }
                        return Mono.empty();
                    });
        }
        return Mono.empty();
    }

    private Mono<Credit> saveNewCredit(Credit credit) {
        credit.setOpeningDate(LocalDate.now());
        credit.setBalance(credit.getAmount());

        if (credit.getType() == Credit.CreditType.CREDIT_CARD) {
            // Set credit card specific dates
            LocalDate now = LocalDate.now();
            credit.setCutDate(now.with(TemporalAdjusters.lastDayOfMonth()));
            credit.setPaymentDate(now.plusMonths(1).withDayOfMonth(15));
            credit.setAvailableCredit(credit.getCreditLimit());
        }

        return creditRepository.save(credit);
    }

    public Mono<Credit> update(String id, Credit credit) {
        return creditRepository.findById(id)
                .flatMap(existingCredit -> {
                    existingCredit.setBalance(credit.getBalance());
                    existingCredit.setRemainingInstallments(credit.getRemainingInstallments());

                    if (existingCredit.getType() == Credit.CreditType.CREDIT_CARD) {
                        existingCredit.setAvailableCredit(credit.getAvailableCredit());
                        existingCredit.setCreditLimit(credit.getCreditLimit());
                    }

                    return creditRepository.save(existingCredit);
                });
    }

    public Mono<Credit> makePayment(String creditId, Double amount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    if (credit.getBalance() < amount) {
                        return Mono.error(new IllegalArgumentException(
                                "Payment amount exceeds credit balance"));
                    }

                    credit.setBalance(credit.getBalance() - amount);

                    if (credit.getType() == Credit.CreditType.CREDIT_CARD) {
                        credit.setAvailableCredit(
                                Math.min(credit.getCreditLimit(),
                                        credit.getAvailableCredit() + amount));
                    }

                    return creditRepository.save(credit);
                });
    }

    public Mono<Credit> chargeConsumption(String creditId, Double amount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    if (credit.getType() != Credit.CreditType.CREDIT_CARD) {
                        return Mono.error(new IllegalArgumentException(
                                "Only credit cards can have consumptions"));
                    }

                    if (credit.getAvailableCredit() < amount) {
                        return Mono.error(new IllegalArgumentException(
                                "Consumption exceeds available credit"));
                    }

                    credit.setBalance(credit.getBalance() + amount);
                    credit.setAvailableCredit(credit.getAvailableCredit() - amount);

                    return creditRepository.save(credit);
                });
    }

    public Mono<Void> delete(String id) {
        return creditRepository.deleteById(id);
    }
}
