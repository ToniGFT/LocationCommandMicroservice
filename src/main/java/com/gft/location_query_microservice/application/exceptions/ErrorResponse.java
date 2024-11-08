package com.gft.location_query_microservice.application.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}

