package ai.shreds.adapters.exceptions;

import org.springframework.http.HttpStatus;

public class AdapterExceptionCategory extends RuntimeException {

    private final String message;
    private final HttpStatus status;

    public AdapterExceptionCategory() {
        super("");
        this.message = "";
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AdapterExceptionCategory(String message, HttpStatus status) {
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