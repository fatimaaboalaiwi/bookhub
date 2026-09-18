package com.company.bookhub.author.service.impl;

import com.company.bookhub.author.dto.AuthorRequest;
import com.company.bookhub.author.dto.AuthorResponse;
import com.company.bookhub.author.entity.Author;
import com.company.bookhub.author.mapper.AuthorMapper;
import com.company.bookhub.author.repository.AuthorRepository;
import com.company.bookhub.author.service.AuthorService;
import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {

        if (authorRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("author.already_exists");
        }

        Author author = authorMapper.toEntity(request);
        author.setCreatedAt(OffsetDateTime.now());

        Author savedAuthor = authorRepository.save(author);

        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException( "author.not_found")
                );

        return authorMapper.toResponse(author);
    }

    @Override
    public List<AuthorResponse> getAllAuthors() {

        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Override
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Author not found with id: " + id)
                );

        if (authorRepository.findByName(request.getName())
                .filter(existingAuthor -> !existingAuthor.getId().equals(id))
                .isPresent()) {

            throw new DuplicateResourceException("Author already exists");
        }

        author.setName(request.getName());
        author.setBiography(request.getBiography());

        Author updatedAuthor = authorRepository.save(author);

        return authorMapper.toResponse(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Author not found with id: " + id)
                );

        authorRepository.delete(author);
    }
}