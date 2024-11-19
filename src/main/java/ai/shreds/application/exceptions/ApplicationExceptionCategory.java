package ai.shreds.application.exceptions;

public class ApplicationExceptionCategory extends RuntimeException {

    private final int status;

    public ApplicationExceptionCategory(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
