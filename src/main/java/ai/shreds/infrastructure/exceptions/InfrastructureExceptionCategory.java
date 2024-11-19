package ai.shreds.infrastructure.exceptions;

public class InfrastructureExceptionCategory extends RuntimeException {

    public String message;

    public InfrastructureExceptionCategory(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
