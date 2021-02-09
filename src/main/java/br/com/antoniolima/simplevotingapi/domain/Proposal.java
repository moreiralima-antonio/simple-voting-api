package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "proposals")
public class Proposal implements Serializable {

    private static final long serialVersionUID = 7149573925290184949L;

    @Id
    private String id;
    private String description;
    private Session session;
}