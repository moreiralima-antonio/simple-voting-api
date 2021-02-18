package br.com.antoniolima.simplevotingapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.Session;
import br.com.antoniolima.simplevotingapi.domain.in.SessionRequest;
import br.com.antoniolima.simplevotingapi.exception.CustomApiException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


@Service
@Slf4j
public class SessionService {

    private final ProposalService proposalService;

    public SessionService(final ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    public boolean sessionExists(final Proposal proposal) {
        Session session = proposal.getSession();

        if (Objects.nonNull(session) && Objects.nonNull(session.getStartDate())) {
            log.info("Session already exists: {}", session);
            return true;
        }

        return false;
    }

    public boolean sessionClosed(final Proposal proposal) {
        Session session = proposal.getSession();

        if (Objects.nonNull(session.getEndDate())) {
            log.info("Session already ended: {}", session);
            return true;
        }

        return false;
    }

    public boolean sessionExpired(final Proposal proposal) {
        Session session = proposal.getSession();

        LocalDateTime updatedDate = LocalDateTime.now();

        if (Duration.between(session.getStartDate(), updatedDate).getSeconds() >= session.getTimeout()) {
            log.info("Session has expired: {}", session);
            return true;
        }

        return false;
    }

    public void newSession(final String proposalId, final SessionRequest request) throws CustomApiException {
        Proposal proposal = proposalService.findProposalById(proposalId);

        if (this.sessionExists(proposal)) {
            throw new CustomApiException(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Session cannot be created because it already exists."
            );
        }

        Session newSession = new Session();

        /* Redefine o tempo de expiração da sessão caso receba o parâmetro na requisição */
        if (Objects.nonNull(request) && request.getTimeout() > 0) {
            newSession.setTimeout(request.getTimeout());
        }

        log.info("Session will expires in: {} seconds", newSession.getTimeout());

        proposal.setSession(newSession);

        proposalService.save(proposal);

        log.info("Session created successfully for proposal: {}", proposalId);
    }
}
