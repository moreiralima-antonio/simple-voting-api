package br.com.antoniolima.simplevotingapi.domain.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VoteRequest {

    @JsonProperty(required = true)
    @JsonInclude(Include.NON_NULL)
    @Pattern(regexp = "yes|no", message = "Field 'choice' must be yes|no.")
    @NotNull(message = "Field 'choice' must be not null.")
    private String choice;

    @JsonProperty(required = true)
    @JsonInclude(Include.NON_NULL)
    @NotBlank(message = "Field 'memberId' must be not blank.")
    private String memberId;
}
