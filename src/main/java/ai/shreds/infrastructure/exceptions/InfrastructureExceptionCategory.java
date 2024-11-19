package ai.shreds.infrastructure.exceptions;

import lombok.Getter;

/**
 * Custom exception class for category-related exceptions in the infrastructure layer.
 */
@Getter
public class InfrastructureExceptionCategory extends RuntimeException {

    private final String message;

    /**
     * Constructs a new InfrastructureExceptionCategory with the specified detail message.
     *
     * @param message the detail message.
     */
    public InfrastructureExceptionCategory(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Constructs a new InfrastructureExceptionCategory with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public InfrastructureExceptionCategory(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
