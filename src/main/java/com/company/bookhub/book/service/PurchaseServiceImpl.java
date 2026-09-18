package com.company.bookhub.book.service;

import com.company.bookhub.accounting.client.AccountingClient;
import com.company.bookhub.accounting.dto.AccountBalanceResponse;
import com.company.bookhub.accounting.dto.DeductBalanceRequest;
import com.company.bookhub.book.dto.PurchaseResponse;
import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.repository.BookRepository;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.user.entity.User;
import com.company.bookhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AccountingClient accountingClient;

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse purchase(Long bookId) {

        // 1. Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        // 2. Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username " + username + " not found"
                        )
                );

        // 3. Find book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + bookId + " not found"
                        )
                );

        // 4. Get price from BookHub database
        var request = new DeductBalanceRequest(
                user.getId(),
                book.getPrice()
        );

        // 5. Ask Accounting to deduct the price
        AccountBalanceResponse accountingResponse =
                accountingClient.deduct(request);

        // 6. Return purchase result
        return new PurchaseResponse(
                book.getId(),
                book.getTitle(),
                book.getPrice(),
                accountingResponse.balance()
        );
    }
}
