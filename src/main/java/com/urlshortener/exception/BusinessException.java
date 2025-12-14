package com.urlshortener.exception;

/**
 * Исключение для бизнес-ошибок
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
