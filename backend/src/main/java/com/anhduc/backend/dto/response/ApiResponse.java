package com.anhduc.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Builder
@Getter
public class ApiResponse<T> {

    @Builder.Default
    private HttpStatus status = HttpStatus.OK;
    private String message;
    private T data;
}
