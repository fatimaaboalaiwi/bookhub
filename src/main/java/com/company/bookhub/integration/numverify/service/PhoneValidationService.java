package com.company.bookhub.integration.numverify.service;

import com.company.bookhub.integration.numverify.dto.NumverifyResponse;

public interface PhoneValidationService {

    NumverifyResponse validatePhoneNumber(String number);
}