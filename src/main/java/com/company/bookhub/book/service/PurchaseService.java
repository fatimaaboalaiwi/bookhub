package com.company.bookhub.book.service;

import com.company.bookhub.book.dto.PurchaseResponse;

public interface PurchaseService {

    PurchaseResponse purchase(Long bookId);
}