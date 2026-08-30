package com.company.bookhub.book.specification;

import com.company.bookhub.book.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> hasAuthorId(Long authorId) {

        return (root, query, criteriaBuilder) ->
                authorId == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("author").get("id"),
                        authorId
                );
    }

    public static Specification<Book> hasCategoryId(Long categoryId) {

        return (root, query, criteriaBuilder) ->
                categoryId == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Book> titleContains(String title) {

        return (root, query, criteriaBuilder) ->
                title == null || title.isBlank()
                        ? null
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }
}