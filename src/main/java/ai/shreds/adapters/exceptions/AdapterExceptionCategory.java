package ai.shreds.adapters.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdapterExceptionCategory extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AdapterExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AdapterExceptionCategory(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}