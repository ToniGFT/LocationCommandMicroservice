package com.gft.location_query_microservice.domain.exception;

public class LocationSaveException extends RuntimeException {
    public LocationSaveException(String message) {
        super(message);
    }

    public LocationSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
