package com.company.bookhub.category.controller;

import com.company.bookhub.category.dto.CategoryRequest;
import com.company.bookhub.category.dto.CategoryResponse;
import com.company.bookhub.category.service.CategoryService;
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
@RequestMapping(ApiPaths.CATEGORIES)
@RequiredArgsConstructor
@Tag(
        name = "Categories",
        description = "Operations for managing categories"
)
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    // =========================
    // CREATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Create a category",
            description = "Creates a new category. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category already exists"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET BY ID - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get category by ID",
            description = "Returns a category by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoryService.getById(id)
        );
    }

    // =========================
    // GET ALL - AUTHENTICATED USERS
    // =========================

    @Operation(
            summary = "Get all categories",
            description = "Returns all categories."
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {

        return ResponseEntity.ok(
                categoryService.getAll()
        );
    }

    // =========================
    // UPDATE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Update a category",
            description = "Updates an existing category. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                categoryService.update(id, request)
        );
    }

    // =========================
    // DELETE - ADMIN ONLY
    // =========================

    @Operation(
            summary = "Delete a category",
            description = "Deletes a category. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}