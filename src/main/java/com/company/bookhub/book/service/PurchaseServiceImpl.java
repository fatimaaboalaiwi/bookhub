
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AccountingClient accountingClient;

    @Override
    @Transactional
    public PurchaseResponse purchase(Long bookId) {

        // 1. Get authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        // 2. Find user.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username " + username + " not found"
                        )
                );

        // 3. Lock the selected book row.
        // A concurrent purchase of this book must wait.
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + bookId + " not found"
                        )
                );
        try {
            sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 4. Check quantity while holding the lock.
        if (book.getQty() == null || book.getQty() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book is out of stock"
            );
        }

        // 5. Decrease inventory.
        book.setQty(book.getQty() - 1);

        // Persist the managed entity's change.
        bookRepository.save(book);

        // 6. Ask Accounting to deduct the price.
        // If Accounting rejects the deduction, this transaction rolls back.
        var request = new DeductBalanceRequest(
                user.getId(),
                book.getPrice()
        );

        AccountBalanceResponse accountingResponse =
                accountingClient.deduct(request);

        // 7. Return purchase result.
        return new PurchaseResponse(
                book.getId(),
                book.getTitle(),
                book.getPrice(),
                accountingResponse.balance()
        );
    }
}
