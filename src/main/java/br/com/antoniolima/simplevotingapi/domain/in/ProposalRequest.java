package br.com.antoniolima.simplevotingapi.domain.in;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProposalRequest {

    @JsonProperty(required = true)
    @JsonInclude(Include.NON_NULL)
    @NotBlank(message = "Field 'description' must have a valid value.")
    private String description;
}
