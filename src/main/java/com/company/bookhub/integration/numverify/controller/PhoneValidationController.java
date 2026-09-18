package com.company.bookhub.integration.numverify.controller;

import com.company.bookhub.integration.numverify.dto.NumverifyResponse;
import com.company.bookhub.integration.numverify.dto.PhoneValidationRequest;
import com.company.bookhub.integration.numverify.service.PhoneValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/phone")
@RequiredArgsConstructor
@Tag(
        name = "Phone Validation",
        description = "Phone number validation APIs"
)
public class PhoneValidationController {

    private final PhoneValidationService phoneValidationService;

    @Operation(
            summary = "Validate phone number",
            description = "Validates a phone number using the Numverify API"
    )
    @GetMapping("/validate")
    public ResponseEntity<NumverifyResponse> validatePhoneNumber(
            @Valid @ModelAttribute PhoneValidationRequest request) {

        return ResponseEntity.ok(
                phoneValidationService.validatePhoneNumber(
                        request.number()
                )
        );
    }
}