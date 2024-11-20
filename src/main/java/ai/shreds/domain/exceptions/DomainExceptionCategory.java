package ai.shreds.domain.exceptions;

import lombok.Getter;

@Getter
public class DomainExceptionCategory extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String errorCode;

    /**
     * Constructor with message, sets errorCode to 'UNKNOWN_ERROR'.
     * @param message the error message
     */
    public DomainExceptionCategory(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }

    /**
     * Constructor with message and errorCode.
     * @param message the error message
     * @param errorCode the specific error code
     */
    public DomainExceptionCategory(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with message and cause, sets errorCode to 'UNKNOWN_ERROR'.
     * @param message the error message
     * @param cause the throwable cause
     */
    public DomainExceptionCategory(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
    }

    /**
     * Constructor with message, errorCode, and cause.
     * @param message the error message
     * @param errorCode the specific error code
     * @param cause the throwable cause
     */
    public DomainExceptionCategory(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}