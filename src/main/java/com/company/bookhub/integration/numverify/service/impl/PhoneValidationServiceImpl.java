package com.company.bookhub.integration.numverify.service.impl;

import com.company.bookhub.integration.numverify.client.NumverifyClient;
import com.company.bookhub.integration.numverify.dto.NumverifyResponse;
import com.company.bookhub.integration.numverify.service.PhoneValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneValidationServiceImpl
        implements PhoneValidationService {

    private final NumverifyClient numverifyClient;

    @Override
    public NumverifyResponse validatePhoneNumber(String number) {

        return numverifyClient.validatePhoneNumber(number);
    }
}