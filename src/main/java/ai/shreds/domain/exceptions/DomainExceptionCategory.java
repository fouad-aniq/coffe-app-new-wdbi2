package ai.shreds.domain.exceptions;

import lombok.Getter;

@Getter
public class DomainExceptionCategory extends RuntimeException {
    private final String errorCode;

    public DomainExceptionCategory(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
