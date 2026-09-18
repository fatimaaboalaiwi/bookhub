package com.company.bookhub.accounting.controller;

import com.company.bookhub.accounting.client.AccountingClient;
import com.company.bookhub.accounting.dto.CheckPurchaseRequest;
import com.company.bookhub.accounting.dto.CheckPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
public class AccountingTestController {

    private final AccountingClient accountingClient;

    @PostMapping("/check-purchase")
    public CheckPurchaseResponse checkPurchase(
            @RequestBody CheckPurchaseRequest request) {

        return accountingClient.checkPurchase(request);
    }
}
