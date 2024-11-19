package ai.shreds.adapters.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdapterExceptionCategory extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message = "An unexpected error occurred";
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AdapterExceptionCategory(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}