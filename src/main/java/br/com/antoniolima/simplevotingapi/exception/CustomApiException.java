package br.com.antoniolima.simplevotingapi.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiException extends Exception {

    private static final long serialVersionUID = -7333619631694960147L;

    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String error;
    private final String message;

    public CustomApiException(final HttpStatus status, final String error, final String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}