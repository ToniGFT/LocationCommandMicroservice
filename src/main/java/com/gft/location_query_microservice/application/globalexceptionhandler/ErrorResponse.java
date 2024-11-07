package com.gft.location_query_microservice.application.globalexceptionhandler;

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
