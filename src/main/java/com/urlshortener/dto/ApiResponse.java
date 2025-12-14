package com.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Стандартный формат ответа API
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Integer errorCode;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp, Integer errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.errorCode = errorCode;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getErrorCode() { return errorCode; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message, Integer errorCode) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), errorCode);
    }
}
