package com.mp.web.exception;

/**
 * Exception thrown when an error occurs during web API operations.
 * Used for operations that show errors directly on the page.
 */
public class WebApiException extends RuntimeException {
    private final String currentPath;
    private final String viewName;

    public WebApiException(String message, String currentPath, String viewName) {
        super(message);
        this.currentPath = currentPath;
        this.viewName = viewName;
    }

    public WebApiException(String message, String currentPath, String viewName, Throwable cause) {
        super(message, cause);
        this.currentPath = currentPath;
        this.viewName = viewName;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public String getViewName() {
        return viewName;
    }
} 