package com.gft.location_query_microservice.application.exceptions;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleLocationSaveException() {
        LocationSaveException ex = new LocationSaveException("Error saving location");
        ResponseEntity<String> response = globalExceptionHandler.handleLocationSaveException(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error saving location update: Error saving location", response.getBody());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input data");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleIllegalArgument(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid JSON format: Invalid input data", response.getBody().getMessage());
    }

    @Test
    void testHandleHttpMessageConversionException() {
        HttpMessageConversionException ex = new HttpMessageConversionException("Conversion error");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleHttpMessageConversionException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid JSON format: Conversion error", response.getBody().getMessage());
    }

    @Test
    void testHandleNumberFormatException() {
        NumberFormatException ex = new NumberFormatException("Invalid number format");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleNumberFormatException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid input", response.getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "Invalid field");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleMethodArgumentNotValid(ex);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validation failed: Invalid field; ", response.getBody().getMessage());
    }

    @Test
    void testHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Element not found");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleNoSuchElementException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Element not found", response.getBody().getMessage());
    }

    @Test
    void testHandleInternalException() {
        ExecutionControl.InternalException ex = new ExecutionControl.InternalException("Internal server error");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleInternalException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}
