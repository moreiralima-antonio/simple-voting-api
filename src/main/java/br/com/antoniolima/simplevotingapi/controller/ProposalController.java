package br.com.antoniolima.simplevotingapi.controller;

import org.springframework.web.bind.annotation.RestController;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.Results;
import br.com.antoniolima.simplevotingapi.domain.Session;
import br.com.antoniolima.simplevotingapi.domain.Vote;
import br.com.antoniolima.simplevotingapi.repository.ProposalRepository;
import br.com.antoniolima.simplevotingapi.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/proposals")
@Slf4j
public class ProposalController {

    @Autowired
    private ProposalRepository proposalRep;

    @Autowired
    private VoteRepository voteRep;

    @PostMapping
    public Proposal newProposal(@RequestBody Proposal newProposal) {
        log.info("Received a new proposal request with data: {} ", newProposal);
        return proposalRep.save(newProposal);
    }

    @GetMapping
    public List<Proposal> getAllProposals() {
        return proposalRep.findAll();
    }

    @GetMapping("/{id}")
    public Proposal findProposalById(@PathVariable String id) {
        Optional<Proposal> proposal = proposalRep.findById(id);

        if (!proposal.isPresent()) {
            log.error("Proposal not found!");
            return null;
        }

        return proposal.get();
    }

    @PostMapping("/{id}/sessions")
    public Proposal newSession(@PathVariable String id, @RequestBody Session newSession) {
        log.info("Received a new session request with data: {} ", newSession);

        Optional<Proposal> proposal = proposalRep.findById(id);

        if (!proposal.isPresent()) {
            log.error("Proposal not found!");
            return null;
        }

        Proposal updatedProposal = proposal.get();

        Session session = updatedProposal.getSession();

        /**
         * Valida se já existe uma sessão aberta.
         */
        if (Objects.nonNull(session) && Objects.nonNull(session.getStartDate())) {
            log.info("Skipping... session already created: {} ", session);
            return null;
        }

        /**
         * Marca o início de uma nova sessão.
         */
        newSession.setStartDate(new Date());

        updatedProposal.setSession(newSession);

        return proposalRep.save(updatedProposal);
    }

    @PostMapping("/{id}/votes")
    public Vote newVote(@PathVariable String id, @RequestBody Vote newVote) {
        newVote.setProposalId(id);
        newVote.setVoteDate(new Date());

        log.info("Received a new vote request with data: {}", newVote);

        Optional<Proposal> proposal = proposalRep.findById(id);

        if (!proposal.isPresent()) {
            log.error("Proposal not found!");
            return null;
        }

        Proposal updatedProposal = proposal.get();
        Session session = updatedProposal.getSession();

        /**
         * Valida se existe uma sessão aberta.
         */
        if (Objects.nonNull(session) && Objects.nonNull(session.getStartDate())) {
            /**
             * Valida sa a sessão terminou, não permitindo novos votos.
             */
            if( Objects.nonNull(session.getEndDate())) {
                log.info("Session already ended: {}", session);
                return null;
            }

            Date updatedDate = new Date();

            /**
             * Valida sa a sessão expirou.
             */
            if (updatedDate.getTime() - session.getStartDate().getTime() >= (session.getTimeout()*1000)) {
                session.setEndDate(updatedDate);
                updatedProposal.setSession(session);
                proposalRep.save(updatedProposal);

                log.info("Session has expired: {}", session);

                return null;
            }

            return voteRep.save(newVote);
        }

        return null;
    }

    @GetMapping("/{id}/votes")
    public List<Vote> getAllVotes(@PathVariable String id) {
        return voteRep.findAll().stream()
            .filter(v -> v.getProposalId().equals(id))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}/results")
    public Results getResults(@PathVariable String id) {
        log.info("Received a new results request for proposal: {}", id);

        /**
         * Contabiliza todos os votos de uma pauta (somente um voto por membro).
        */
        List<Vote> votes = voteRep.findAll();

        Comparator<Vote> compareByDate = Comparator
            .comparing(Vote::getVoteDate);

        List<Vote> uniqueVotes = votes.stream()
            .filter(v -> v.getProposalId().equals(id))
            .sorted(compareByDate)
            .distinct()
            .collect(Collectors.toList());

        Results results = new Results();

        long yes = uniqueVotes.stream()
            .filter(v -> v.getChoice().equalsIgnoreCase("yes"))
            .count();

        results.setYes(yes);

        long no = uniqueVotes.stream()
            .filter(v -> v.getChoice().equalsIgnoreCase("no"))
            .count();

        results.setNo(no);

        log.debug("Original vote list: {}", votes);
        log.debug("Unique vote list: {}", uniqueVotes);

        return results;
    }
}
