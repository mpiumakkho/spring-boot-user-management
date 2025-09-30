package com.mp.web.exception;

/**
 * Exception thrown when an error occurs during form submission.
 * Used for operations that redirect back with flash messages.
 */
public class FormSubmissionException extends RuntimeException {
    private final String redirectPath;

    public FormSubmissionException(String message, String redirectPath) {
        super(message);
        this.redirectPath = redirectPath;
    }

    public FormSubmissionException(String message, String redirectPath, Throwable cause) {
        super(message, cause);
        this.redirectPath = redirectPath;
    }

    public String getRedirectPath() {
        return redirectPath;
    }
} 