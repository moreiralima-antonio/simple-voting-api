package br.com.antoniolima.simplevotingapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "votes")
public class Vote implements Serializable {

    private static final long serialVersionUID = -6152513520640057809L;

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    @EqualsAndHashCode.Exclude
    private String choice;

    @EqualsAndHashCode.Exclude
    private LocalDateTime voteDate;

    private String memberId;
    private String proposalId;
}
