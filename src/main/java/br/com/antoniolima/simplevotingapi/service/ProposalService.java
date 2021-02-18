package br.com.antoniolima.simplevotingapi.service;

import org.springframework.stereotype.Service;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.exception.CustomApiException;
import br.com.antoniolima.simplevotingapi.repository.ProposalRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.http.HttpStatus;

@Service
@Slf4j
public class ProposalService {

    private final ProposalRepository proposalRep;

    public ProposalService(final ProposalRepository proposalRep) {
        this.proposalRep = proposalRep;
    }

    public Proposal findProposalById(final String id) throws CustomApiException {

        Optional<Proposal> proposal = proposalRep.findById(id);

        if (!proposal.isPresent()) {
            log.error("Proposal id not found: {}", id);

            throw new CustomApiException(
                HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Proposal id not found."
            );
        }

        return proposal.get();
    }

    public Proposal save(final Proposal newProposal) {
        return proposalRep.save(newProposal);
    }
}
