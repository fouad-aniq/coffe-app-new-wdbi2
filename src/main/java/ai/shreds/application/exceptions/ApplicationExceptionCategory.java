package ai.shreds.application.exceptions;

import org.springframework.http.HttpStatus;

public class ApplicationExceptionCategory extends RuntimeException {

    private final String message;
    private final HttpStatus status;

    public ApplicationExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}