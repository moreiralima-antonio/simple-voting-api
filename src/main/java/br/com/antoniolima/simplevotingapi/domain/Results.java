package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Results implements Serializable {

    private static final long serialVersionUID = -3166692173217291130L;

    private Long yes;
    private Long no;
}
