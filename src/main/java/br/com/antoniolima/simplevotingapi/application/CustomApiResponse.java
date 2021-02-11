package br.com.antoniolima.simplevotingapi.application;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class CustomApiResponse {

    private Date timestamp;
    private HttpStatus status;
    private String error;
    private String message;

    public CustomApiResponse() {
        super();
    }

    public CustomApiResponse(final HttpStatus status, final String error, final String message) {
        super();
        this.timestamp =  new Date();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}