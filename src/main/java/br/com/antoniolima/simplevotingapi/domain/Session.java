package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Session implements Serializable {

    private static final long serialVersionUID = -2420395252951715021L;

    private static final long DEFAULT_EXPIRATION_TIME = 60;

    private long timeout = DEFAULT_EXPIRATION_TIME;
    private Date startDate;
    private Date endDate;

    public Session() {
        this.startDate = new Date();
    }
}
