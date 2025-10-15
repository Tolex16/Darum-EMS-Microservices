package com.darum.ems.auth.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // (Optional) Handle other RuntimeExceptions gracefully
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    body.put("path", request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
}


	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(
      new GlobalExceptionHandler.ErrorResponse("BAD_REQUEST", ex.getMessage())
    );
  }

  record ErrorResponse(String code, String message) {}
}
