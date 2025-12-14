package com.urlshortener.exception;

/**
 * Исключение для случаев когда ресурс не найден
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
