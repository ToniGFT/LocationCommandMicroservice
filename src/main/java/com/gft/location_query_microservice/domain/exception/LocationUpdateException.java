package com.gft.location_query_microservice.domain.exception;

public class LocationUpdateException extends RuntimeException {

    public LocationUpdateException(String message) {
        super(message);
    }

    public LocationUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
