package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Session implements Serializable {

    private static final long serialVersionUID = -2420395252951715021L;

    private static final long DEFAULT_EXPIRATION_TIME = 60;

    private long timeout = DEFAULT_EXPIRATION_TIME;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Session() {
        this.startDate = LocalDateTime.now();
    }
}
