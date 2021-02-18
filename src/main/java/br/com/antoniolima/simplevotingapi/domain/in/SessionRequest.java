package br.com.antoniolima.simplevotingapi.domain.in;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SessionRequest {

    @JsonProperty(required = true)
    @JsonInclude(Include.NON_NULL)
    @NotNull(message = "Field 'timeout' must have a valid value.")
    private long timeout;
}
