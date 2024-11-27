// Full fixed and enhanced code for the main class 'InvalidMetadataException'
package ai.shreds.shared;

public class InvalidMetadataException extends Exception {

    private static final long serialVersionUID = 1L;

    // Default constructor
    public InvalidMetadataException() {
        super();
    }

    // Constructor with message
    public InvalidMetadataException(String message) {
        super(message);
    }

    // Constructor with cause
    public InvalidMetadataException(Throwable cause) {
        super(cause);
    }

    // Constructor with message and cause
    public InvalidMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with suppression enabled or disabled and writable stack trace
    protected InvalidMetadataException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}