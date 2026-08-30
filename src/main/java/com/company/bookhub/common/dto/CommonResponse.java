package com.company.bookhub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
// A Java Record is perfect for DTOs (Data Transfer Objects)
public class CommonResponse<T> {

    private int code;
    private String message;
    private T data;

}