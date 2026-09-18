
package com.company.bookhub.book.mapper;

import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "category.name", target = "categoryName")
    BookResponse toResponse(Book book);
}
