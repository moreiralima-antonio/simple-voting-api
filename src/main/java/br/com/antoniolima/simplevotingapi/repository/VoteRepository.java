package br.com.antoniolima.simplevotingapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.antoniolima.simplevotingapi.domain.Vote;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    List<Vote> findByProposalId(String proposalId);
}
