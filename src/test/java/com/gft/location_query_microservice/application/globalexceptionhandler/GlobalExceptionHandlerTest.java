package com.gft.location_query_microservice.application.globalexceptionhandler;

import com.gft.location_query_microservice.domain.exception.LocationSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Handle LocationSaveException")
    void testHandleLocationSaveException() {
        // Arrange
        LocationSaveException exception = new LocationSaveException("Database error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleLocationSaveException(exception).block();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Error saving location update: Database error occurred");
    }

    @Test
    @DisplayName("Handle MethodArgumentNotValidException")
    void testHandleValidationExceptions() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("locationUpdate", "vehicleId", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception).block();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getMessage()).contains("Validation failed: vehicleId - must not be null");
    }

    @Test
    @DisplayName("Handle Generic Exception")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, null).block();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred: Unexpected error occurred");
    }
}
