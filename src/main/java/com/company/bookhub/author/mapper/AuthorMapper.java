package com.company.bookhub.author.mapper;

import com.company.bookhub.author.dto.AuthorRequest;
import com.company.bookhub.author.dto.AuthorResponse;
import com.company.bookhub.author.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "biography", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);
}