package br.com.antoniolima.simplevotingapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.antoniolima.simplevotingapi.domain.Proposal;

@Repository
public interface ProposalRepository extends MongoRepository<Proposal, String> {
}
