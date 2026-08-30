package com.company.bookhub.book.controller;

import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.service.BookService;
import com.company.bookhub.common.constant.ApiPaths;
import com.company.bookhub.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(ApiPaths.BOOKS)
@RequiredArgsConstructor
@Tag(
        name = "Books",
        description = "Operations for managing books"
)
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    // =========================
    // CREATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Create a book",
            description = "Creates a new book. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author or category not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "ISBN already exists"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request) {

        BookResponse response = bookService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET BY ID - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get a book by ID",
            description = "Returns a book using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid book ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookService.getById(id)
        );
    }

    // =========================
    // GET ALL - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get books",
            description = "Returns books with pagination, sorting and filtering."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Books retrieved successfully"
            )
    })
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
    // UPDATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Update a book",
            description = "Updates an existing book. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book, author or category not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "ISBN already exists"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        return ResponseEntity.ok(
                bookService.update(id, request)
        );
    }

    // =========================
    // DELETE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book by its ID. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        bookService.delete(id);

        return ResponseEntity.noContent().build();
    }
}