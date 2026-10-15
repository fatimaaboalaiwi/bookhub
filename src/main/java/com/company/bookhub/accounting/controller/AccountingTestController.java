package com.company.bookhub.accounting.controller;

import com.company.bookhub.accounting.client.AccountingClient;
import com.company.bookhub.accounting.dto.CheckPurchaseRequest;
import com.company.bookhub.accounting.dto.CheckPurchaseResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
@Tag(
        name = "Accounting",
        description = "APIs for checking purchase eligibility and account balance"
)
public class AccountingTestController {

    private final AccountingClient accountingClient;

    @PostMapping("/check-purchase")
    public CheckPurchaseResponse checkPurchase(
            @RequestBody CheckPurchaseRequest request) {

        return accountingClient.checkPurchase(request);
    }
}
