
package com.company.bookhub.Controller.catalog;

import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.dto.PurchaseResponse;
import com.company.bookhub.book.service.BookService;
import com.company.bookhub.book.service.PurchaseService;
import com.company.bookhub.common.constant.ApiPaths;
import com.company.bookhub.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.company.bookhub.common.dto.ApiSuccessResponse;

@RestController
@RequestMapping(ApiPaths.BOOKS)
@RequiredArgsConstructor
@Tag(
        name = "Catalog",
        description = "APIs for managing books, authors and categories"
)
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;
    private final PurchaseService purchaseService;

    // =========================
    // CREATE
    // =========================

    @Operation(
            summary = "Create a book",
            description = "Creates a new book. Requires BOOK_CREATE permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Author or category not found"),
            @ApiResponse(responseCode = "409", description = "ISBN already exists")
    })
    @PreAuthorize("hasAuthority('BOOK_CREATE')")
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request) {

        BookResponse response = bookService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET BY ID
    // =========================

    @Operation(
            summary = "Get a book by ID",
            description = "Returns a book using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PreAuthorize("hasAuthority('BOOK_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookService.getById(id)
        );
    }

    // =========================
    // GET ALL
    // =========================

    @Operation(
            summary = "Get books",
            description = "Returns books with pagination, sorting and filtering."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    @PreAuthorize("hasAuthority('BOOK_READ')")
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAll(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            Pageable pageable) {

        return ResponseEntity.ok(
                bookService.getAll(
                        authorId,
                        categoryId,
                        title,
                        pageable
                )
        );
    }

    // =========================
    // UPDATE
    // =========================

    @Operation(
            summary = "Update a book",
            description = "Updates an existing book. Requires BOOK_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Book, author or category not found"),
            @ApiResponse(responseCode = "409", description = "ISBN already exists")
    })
    @PreAuthorize("hasAuthority('BOOK_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        return ResponseEntity.ok(
                bookService.update(id, request)
        );
    }

    // =========================
    // DELETE
    // =========================

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book by its ID. Requires BOOK_DELETE permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PreAuthorize("hasAuthority('BOOK_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse> delete(
            @PathVariable Long id) {

        bookService.delete(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse("Book deleted successfully")
        );
    }

    // =========================
    // PURCHASE
    // =========================

    @Operation(
            summary = "Purchase a book",
            description = "Purchases a book using the authenticated user's account balance."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book purchased successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Missing BOOK_PURCHASE permission"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book, user or account not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Insufficient balance"
            )
    })
    @PostMapping("/{bookId}/purchase")
    @PreAuthorize("hasAuthority('BOOK_PURCHASE')")
    public ResponseEntity<PurchaseResponse> purchase(
            @PathVariable Long bookId) {

        return ResponseEntity.ok(
                purchaseService.purchase(bookId)
        );
    }
}