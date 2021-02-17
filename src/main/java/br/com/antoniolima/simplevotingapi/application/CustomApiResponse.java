package br.com.antoniolima.simplevotingapi.application;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class CustomApiResponse {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;

    public CustomApiResponse() {
        super();
    }

    public CustomApiResponse(final HttpStatus status, final String error, final String message) {
        super();
        this.timestamp =  LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}