package ai.shreds.infrastructure.exceptions;

/**
 * Custom exception class for category-related infrastructure exceptions.
 */
public class InfrastructureExceptionCategory extends RuntimeException {

    /**
     * serialVersionUID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public InfrastructureExceptionCategory() {
        super();
    }

    /**
     * Constructor that accepts a message.
     * 
     * @param message the detail message
     */
    public InfrastructureExceptionCategory(String message) {
        super(message);
    }

    /**
     * Constructor that accepts a cause.
     * 
     * @param cause the cause of the exception
     */
    public InfrastructureExceptionCategory(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor that accepts a message and a cause.
     * 
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public InfrastructureExceptionCategory(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with advanced options.
     * 
     * @param message            the detail message
     * @param cause              the cause of the exception
     * @param enableSuppression  whether or not suppression is enabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public InfrastructureExceptionCategory(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}