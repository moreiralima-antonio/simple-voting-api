package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Session implements Serializable {

    private static final long serialVersionUID = -2420395252951715021L;

    private long timeout;
    private Date startDate;
    private Date endDate;
}
