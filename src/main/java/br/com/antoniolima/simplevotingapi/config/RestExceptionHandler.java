package br.com.antoniolima.simplevotingapi.config;

import br.com.antoniolima.simplevotingapi.domain.out.CustomApiErrorResponse;
import br.com.antoniolima.simplevotingapi.exception.CustomApiException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<CustomApiErrorResponse> handleCustomApiException(CustomApiException ex) {

        log.error("CustomApiException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(new CustomApiErrorResponse(ex.getTimestamp(), ex.getStatus(),
            ex.getError(), ex.getMessage()), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiErrorResponse> handleCustomApiException(MethodArgumentNotValidException ex) {

        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(new CustomApiErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST,
            HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getFieldErrors().toString()), HttpStatus.BAD_REQUEST);
    }
}
