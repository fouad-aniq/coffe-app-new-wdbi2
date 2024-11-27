
package ai.shreds.infrastructure.exceptions;

public class CustomKafkaException extends RuntimeException {

    public CustomKafkaException(String message) {
        super(message);
    }

    public CustomKafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
