package ai.shreds.domain.exceptions;

import lombok.Getter;

@Getter
public class DomainExceptionCategory extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String errorCode;

    public DomainExceptionCategory(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public DomainExceptionCategory(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}