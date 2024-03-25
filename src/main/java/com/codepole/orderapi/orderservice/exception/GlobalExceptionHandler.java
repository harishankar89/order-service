package com.codepole.orderapi.orderservice.exception;

import com.codepole.orderapi.orderservice.model.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        logger.error("Validation error: ", ex);
        String errorMessage = ex.getName() + ": should be of type " + ex.getRequiredType().getSimpleName();
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage, request.getDescription(false))
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        logger.error("ConstraintViolationException: ", ex);
        String errorMessage = ex.getConstraintViolations().stream().map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()).collect(Collectors.joining(", "));
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage, request.getDescription(false))
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(HttpMessageNotReadableException ex, WebRequest request) {
        logger.error("ConstraintViolationException: ", ex);
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage, request.getDescription(false))
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<ErrorResponse> handleOrderServiceException(OrderServiceException ex, WebRequest request) {
        logger.error("OrderServiceException: ", ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getDescription(false))
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Exception: ", ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage(), request.getDescription(false))
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ErrorResponse buildResponse(int status, String error, String errorMessage, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status);
        errorResponse.setError(error);
        errorResponse.setMessage(errorMessage);
        errorResponse.setPath(path);
        return errorResponse;
    }
}
