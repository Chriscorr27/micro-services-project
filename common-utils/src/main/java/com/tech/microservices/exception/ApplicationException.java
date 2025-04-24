package com.tech.microservices.exception;

public class ApplicationException extends Exception{
    public ApplicationException(String message){
        super(message);
    }
}
