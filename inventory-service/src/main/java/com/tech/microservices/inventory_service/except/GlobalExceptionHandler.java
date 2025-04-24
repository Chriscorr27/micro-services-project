package com.tech.microservices.inventory_service.except;

import com.tech.microservices.dto.response.MessageResponse;
import com.tech.microservices.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNullBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Request body is missing or invalid"));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<MessageResponse> handleApplicationError(ApplicationException ex){
        MessageResponse response = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleHandlerMethodValidationException(HandlerMethodValidationException ex){
        Map<String, String> errors = new HashMap<>();
        for(Object err : ex.getDetailMessageArguments()){
            String errString = err.toString();
            String[] errStringSplit = errString
                    .replaceAll("\\s*,\\s*and\\s*",",")
                    .replaceAll("\\s*,\\s*", ",").split(",");
            System.out.println(Arrays.toString(errStringSplit));
            for(String errStr : errStringSplit) {
                if (errStr.contains(":")) {
                    String[] errSplit = errStr.split(":");
                    errors.put(errSplit[0], errSplit[1]);
                } else {
                    if (errors.containsKey("error")) {
                        errors.put("error", errors.get("error") + ", " + errStr);
                    } else {
                        errors.put("error", errStr);
                    }
                }
            }
        }
        return ResponseEntity.badRequest().body(errors);
    }

    // Catch-all for unexpected validation issues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
    }
}
