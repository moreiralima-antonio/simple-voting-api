package br.com.antoniolima.simplevotingapi.service;

import org.springframework.stereotype.Service;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.Session;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


@Service
@Slf4j
@NoArgsConstructor
public class SessionService {

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
}
