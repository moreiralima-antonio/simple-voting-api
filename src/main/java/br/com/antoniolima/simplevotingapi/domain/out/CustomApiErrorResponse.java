package br.com.antoniolima.simplevotingapi.domain.out;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomApiErrorResponse {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
}