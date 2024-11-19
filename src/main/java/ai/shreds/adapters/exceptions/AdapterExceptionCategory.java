package ai.shreds.adapters.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AdapterExceptionCategory extends RuntimeException {

    private final HttpStatus status;

    public AdapterExceptionCategory() {
        super("An error occurred");
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AdapterExceptionCategory(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AdapterExceptionCategory(HttpStatus status) {
        super("An error occurred");
        this.status = status;
    }

    public AdapterExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AdapterExceptionCategory(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public AdapterExceptionCategory(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }
}