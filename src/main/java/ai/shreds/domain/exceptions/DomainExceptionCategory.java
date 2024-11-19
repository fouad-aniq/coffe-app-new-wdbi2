package ai.shreds.domain.exceptions;

import lombok.Getter;

/**
 * Exception class for domain-related category errors.
 */
@Getter
public class DomainExceptionCategory extends RuntimeException {
    
    // Added serialVersionUID for serialization compatibility
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;

    /**
     * Constructs a new DomainExceptionCategory with the specified detail message and error code.
     *
     * @param message   the detail message.
     * @param errorCode the specific error code associated with this exception.
     */
    public DomainExceptionCategory(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}