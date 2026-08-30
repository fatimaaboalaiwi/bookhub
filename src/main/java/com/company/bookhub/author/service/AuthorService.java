package com.company.bookhub.author.service;

import com.company.bookhub.author.dto.AuthorRequest;
import com.company.bookhub.author.dto.AuthorResponse;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse getAuthorById(Long id);

    List<AuthorResponse> getAllAuthors();

    AuthorResponse updateAuthor(Long id, AuthorRequest request);

    void deleteAuthor(Long id);
}