package ai.shreds.application.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * Custom exception for category-related errors in the application layer.
 */
@Getter
public class ApplicationExceptionCategory extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * The HTTP status associated with the exception.
     */
    private final HttpStatus status;

    /**
     * Constructs a new ApplicationExceptionCategory with the specified detail message and HTTP status.
     *
     * @param message the detail message.
     * @param status the HTTP status code.
     */
    public ApplicationExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructs a new ApplicationExceptionCategory with the specified detail message and status code.
     * Converts the status code to HttpStatus.
     *
     * @param message the detail message.
     * @param statusCode the HTTP status code as int.
     */
    public ApplicationExceptionCategory(String message, int statusCode) {
        super(message);
        // Convert int status code to HttpStatus
        HttpStatus resolvedStatus = HttpStatus.resolve(statusCode);
        if (resolvedStatus != null) {
            this.status = resolvedStatus;
        } else {
            // Assign default status if invalid code is provided
            this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}