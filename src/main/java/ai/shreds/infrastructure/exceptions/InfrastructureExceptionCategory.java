package ai.shreds.infrastructure.exceptions;

/**
 * Custom exception class for category-related exceptions in the infrastructure layer.
 */
public class InfrastructureExceptionCategory extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InfrastructureExceptionCategory with the specified detail message.
     *
     * @param message the detail message.
     */
    public InfrastructureExceptionCategory(String message) {
        super(message);
    }

    /**
     * Constructs a new InfrastructureExceptionCategory with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public InfrastructureExceptionCategory(String message, Throwable cause) {
        super(message, cause);
    }
}