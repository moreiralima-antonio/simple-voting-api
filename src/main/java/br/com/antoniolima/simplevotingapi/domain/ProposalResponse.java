package br.com.antoniolima.simplevotingapi.domain;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProposalResponse {

	@JsonProperty(required = true)
    @JsonInclude(Include.NON_NULL)
    @NotBlank
    private String id;

    public ProposalResponse(final String id) {
        this.id = id;
    }
}