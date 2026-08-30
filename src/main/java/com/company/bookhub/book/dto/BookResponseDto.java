package com.company.bookhub.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BookResponseDto  {

    private Long id;
    private String title;
    private Long authorId;
    private Long categoryId;
    private String isbn;

}

