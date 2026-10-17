package com.hirehub.hirehub_api.exception;


import com.hirehub.hirehub_api.dto.error.ErrorResponse;
import com.hirehub.hirehub_api.dto.error.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String,String> fieldErrors = new HashMap<>();
        for(FieldError error: ex.getBindingResult().getFieldErrors()){
            fieldErrors.put(error.getField(),error.getDefaultMessage());
        }
        ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "One or more input fields failed validation constraints",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception,HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(),
                "Not found",
                exception.getMessage(),
                request.getRequestURI()
                );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestHandler(HttpServletRequest request, BadRequestException exception){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage(),
                request.getRequestURI()
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception,HttpServletRequest request){

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                "Conflict",exception.getMessage(),request.getRequestURI()
                );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(),
                "Access Denied",ex.getMessage(),request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                "UnAuthorized",ex.getMessage(),request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception,HttpServletRequest request){

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                "A database integrity constraint was violated (duplicate record or invalid reference)",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex,HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected Error occurred. Please Contact the Support team "+ex.getMessage() ,
                request.getRequestURI()
                );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }



}
