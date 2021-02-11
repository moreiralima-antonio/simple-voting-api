package br.com.antoniolima.simplevotingapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.antoniolima.simplevotingapi.domain.Results;
import br.com.antoniolima.simplevotingapi.domain.Vote;
import br.com.antoniolima.simplevotingapi.repository.VoteRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
@NoArgsConstructor
public class VoteService {

    @Autowired
    private VoteRepository voteRep;

    public Vote save(final Vote newVote) {
        return voteRep.save(newVote);
    }

    public List<Vote> findByProposalId(final String id) {
        return voteRep.findByProposalId(id);
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
