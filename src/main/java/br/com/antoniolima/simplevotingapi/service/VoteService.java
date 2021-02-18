package br.com.antoniolima.simplevotingapi.service;

import org.springframework.stereotype.Service;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.Results;
import br.com.antoniolima.simplevotingapi.domain.Session;
import br.com.antoniolima.simplevotingapi.domain.Vote;
import br.com.antoniolima.simplevotingapi.domain.in.VoteRequest;
import br.com.antoniolima.simplevotingapi.exception.CustomApiException;
import br.com.antoniolima.simplevotingapi.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

@Service
@Slf4j
public class VoteService {

    private final VoteRepository voteRep;
    private final ProposalService proposalService;
    private final SessionService sessionService;

    public VoteService(final VoteRepository voteRep, final ProposalService proposalService,
        final SessionService sessionService) {

        this.voteRep = voteRep;
        this.proposalService = proposalService;
        this.sessionService = sessionService;
    }

    public Vote save(final Vote newVote) {
        return voteRep.save(newVote);
    }

    public List<Vote> findByProposalId(final String id) {
        return voteRep.findByProposalId(id);
    }

    public void newVote(final String proposalId, final VoteRequest request) throws CustomApiException {
        Proposal proposal = proposalService.findProposalById(proposalId);

        if (!sessionService.sessionExists(proposal)) {
            throw new CustomApiException(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session is not open yet."
            );
        }

        if (sessionService.sessionClosed(proposal)) {
            throw new CustomApiException(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session is closed."
            );
        }

        if (sessionService.sessionExpired(proposal)) {
            Session session = proposal.getSession();
            session.setEndDate(LocalDateTime.now());
            proposal.setSession(session);
            proposalService.save(proposal);

            throw new CustomApiException(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session has expired."
            );
        }

        Vote newVote = Vote.builder()
            .proposalId(proposalId)
            .voteDate(LocalDateTime.now())
            .choice(request.getChoice())
            .memberId(request.getMemberId())
            .build();

        this.save(newVote);

        log.info("Vote registered successfully: {}", newVote);
    }

    public Results computeVotes(final String id) {
        List<Vote> votes = this.findByProposalId(id);

        Comparator<Vote> compareByDate = Comparator
            .comparing(Vote::getVoteDate);

        List<Vote> uniqueVotes = votes.stream()
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

        log.info("Voting session results (proposal {}) obtained: {}", id, results);

        return results;
    }
}
