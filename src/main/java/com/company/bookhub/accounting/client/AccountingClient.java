package com.company.bookhub.accounting.client;

import com.company.bookhub.accounting.dto.AccountBalanceResponse;
import com.company.bookhub.accounting.dto.CheckPurchaseRequest;
import com.company.bookhub.accounting.dto.CheckPurchaseResponse;
import com.company.bookhub.accounting.dto.DeductBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AccountingClient {

    private final RestClient accountingRestClient;

    public CheckPurchaseResponse checkPurchase(
            CheckPurchaseRequest request) {

        return accountingRestClient.post()
                .uri("/api/v1/accounts/check-purchase")
                .body(request)
                .retrieve()
                .body(CheckPurchaseResponse.class);
    }

    public AccountBalanceResponse deduct(
            DeductBalanceRequest request) {

        return accountingRestClient.post()
                .uri("/api/v1/accounts/deduct")
                .body(request)
                .retrieve()
                .body(AccountBalanceResponse.class);
    }
}