package br.com.antoniolima.simplevotingapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.repository.ProposalRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Service
@Slf4j
@NoArgsConstructor
public class ProposalService {

    @Autowired
    private ProposalRepository proposalRep;

    public Proposal findProposalById(final String id) throws ResponseStatusException {

        Optional<Proposal> proposal = proposalRep.findById(id);

        if (!proposal.isPresent()) {
            log.error("Proposal id not found: {}", id);

            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Proposal id not found.");
        }

        return proposal.get();
    }

    public Proposal save(final Proposal newProposal) {
        return proposalRep.save(newProposal);
    }
}
