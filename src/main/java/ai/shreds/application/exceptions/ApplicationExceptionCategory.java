package ai.shreds.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for category-related exceptions in the application layer.
 */
@Getter
public class ApplicationExceptionCategory extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructs a new ApplicationExceptionCategory with the specified detail message and HTTP status.
     *
     * @param message the detail message.
     * @param status  the HTTP status code.
     */
    public ApplicationExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructs a new ApplicationExceptionCategory with the specified detail message, HTTP status, and cause.
     *
     * @param message the detail message.
     * @param status  the HTTP status code.
     * @param cause   the cause of the exception.
     */
    public ApplicationExceptionCategory(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}