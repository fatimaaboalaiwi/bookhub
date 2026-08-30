package com.company.bookhub.category.mapper;

import com.company.bookhub.category.dto.CategoryRequest;
import com.company.bookhub.category.dto.CategoryResponse;
import com.company.bookhub.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}