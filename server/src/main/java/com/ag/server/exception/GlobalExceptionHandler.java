package com.ag.server.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(InvalidApiSpecException.class)
   public ResponseEntity<?> handleInvalid(InvalidApiSpecException ex) {
      return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now().toString(), "error", "Invalid API Spec", "message", ex.getMessage()));
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<?> handleOther(Exception ex) {
      return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("timestamp", Instant.now().toString(), "error", "Internal Server Error", "message", ex.getMessage()));
   }
}
