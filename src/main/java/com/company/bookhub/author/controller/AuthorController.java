package com.company.bookhub.author.controller;

import com.company.bookhub.author.dto.AuthorRequest;
import com.company.bookhub.author.dto.AuthorResponse;
import com.company.bookhub.author.service.AuthorService;
import com.company.bookhub.common.constant.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.AUTHORS)
@RequiredArgsConstructor
@Tag(
        name = "Authors",
        description = "Operations for managing authors"
)
@SecurityRequirement(name = "bearerAuth")
public class AuthorController {

    private final AuthorService authorService;

    // =========================
    // CREATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Create an author",
            description = "Creates a new author. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Author created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Author already exists"
            )
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(
            @Valid @RequestBody AuthorRequest request) {

        AuthorResponse response = authorService.createAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET BY ID - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get author by ID",
            description = "Returns an author by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                authorService.getAuthorById(id)
        );
    }

    // =========================
    // GET ALL - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get all authors",
            description = "Returns all authors."
    )
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {

        return ResponseEntity.ok(
                authorService.getAllAuthors()
        );
    }

    // =========================
    // UPDATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Update an author",
            description = "Updates an existing author. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {

        return ResponseEntity.ok(
                authorService.updateAuthor(id, request)
        );
    }

    // =========================
    // DELETE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Delete an author",
            description = "Deletes an author. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable Long id) {

        authorService.deleteAuthor(id);

        return ResponseEntity.noContent().build();
    }
}