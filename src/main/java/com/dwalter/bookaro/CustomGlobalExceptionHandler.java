package com.dwalter.bookaro;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
class CustomGlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        body.put("timestamp", new Date());
        body.put("status", status.value());
        List<String> errors = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getField() + " - " + x.getDefaultMessage())
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }
}
