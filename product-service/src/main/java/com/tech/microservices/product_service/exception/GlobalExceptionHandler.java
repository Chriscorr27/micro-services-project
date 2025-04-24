package com.tech.microservices.product_service.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.microservices.dto.response.MessageResponse;
import com.tech.microservices.exception.ApplicationException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
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

    //catch HttpClientErrorException
    @ExceptionHandler(HttpClientErrorException.class)
    public  ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException ex) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(ex.getResponseBodyAsString());
        if(node.get("message")!=null){
            MessageResponse response = new MessageResponse(node.get("message").asText());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else if(node.get("error")!=null){
            MessageResponse response = new MessageResponse(node.get("error").asText());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<?> handleResourceAccessException(ResourceAccessException ex){
        System.out.println(ex);
        if(ex.getMessage().contains("/api/internal-inventory")){
            return ResponseEntity.internalServerError().body(Map.of("error", "Inventory service is not available"));
        }else if(ex.getMessage().contains("/api/product")){
            return ResponseEntity.internalServerError().body(Map.of("error", "Product service is not available"));
        }else if(ex.getMessage().contains("/api/auth")){
            return ResponseEntity.internalServerError().body(Map.of("error", "Auth service is not available"));
        }else{
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }


    // Catch-all for unexpected validation issues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        System.out.println(ex);
        return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
    }
}
