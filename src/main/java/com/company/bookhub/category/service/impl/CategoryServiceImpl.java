package com.company.bookhub.category.service.impl;

import com.company.bookhub.category.dto.CategoryRequest;
import com.company.bookhub.category.dto.CategoryResponse;
import com.company.bookhub.category.entity.Category;
import com.company.bookhub.category.mapper.CategoryMapper;
import com.company.bookhub.category.repository.CategoryRepository;
import com.company.bookhub.category.service.CategoryService;
import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(
                    "Category with name '" + request.name() + "' already exists"
            );
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found"
                        )
                );

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found"
                        )
                );

        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {

            throw new DuplicateResourceException(
                    "Category with name '" + request.name() + "' already exists"
            );
        }

        category.setName(request.name());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found"
                        )
                );

        categoryRepository.delete(category);
    }
}
