package ai.shreds.shared;

/**
 * Exception thrown when metadata validation fails.
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Constructors

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ValidationException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ValidationException with no detail message or cause.
     */
    public ValidationException() {
        super();
    }
}