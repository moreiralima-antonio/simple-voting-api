package br.com.antoniolima.simplevotingapi.controller;

import org.springframework.web.bind.annotation.RestController;

import br.com.antoniolima.simplevotingapi.application.CustomApiResponse;
import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.ProposalRequest;
import br.com.antoniolima.simplevotingapi.domain.ProposalResponse;
import br.com.antoniolima.simplevotingapi.domain.Results;
import br.com.antoniolima.simplevotingapi.domain.Session;
import br.com.antoniolima.simplevotingapi.domain.SessionRequest;
import br.com.antoniolima.simplevotingapi.domain.Vote;
import br.com.antoniolima.simplevotingapi.domain.VoteRequest;
import br.com.antoniolima.simplevotingapi.service.ProposalService;
import br.com.antoniolima.simplevotingapi.service.SessionService;
import br.com.antoniolima.simplevotingapi.service.VoteService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private VoteService voteService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ProposalService proposalService;

    /**
     * Responsável por criar e persistir informações sobre uma pauta.
     *
     * @param request dados de entrada contendo a descrição da pauta.
     *
     * @return
     *   Retorna um identificador único da pauta criada.
     */
    @PostMapping
    public ProposalResponse newProposal(@Valid @RequestBody ProposalRequest request) {
        log.info("Received a new proposal request with data: {}", request);

        Proposal newProposal = new Proposal(request.getDescription());
        Proposal savedProposal = proposalService.save(newProposal);

        log.info("Proposal created with id: {}", savedProposal.getId());

        return new ProposalResponse(savedProposal.getId());
    }

    /**
     * Responsável por criar e persistir informações sobre uma sessão dentro de uma pauta previamente criada.
     * Aqui também é realizado um controle para não permitir abertura de mais de uma sessão em uma mesma pauta.
     *
     * @param id identificador da pauta
     * @param request dados de entrada contendo o tempo de expiração (segundos) da sessão de votação (opcional).
     *
     * @return
     *   Retorno padrão HTTP com base no objeto CustomApiResponse.
     */
    @PostMapping("/{id}/sessions")
    public ResponseEntity<CustomApiResponse> newSession(@PathVariable String id, @Valid @RequestBody (required = false) SessionRequest request) {
        log.info("Received a new session request for proposal: {}", id);

        Proposal proposal = proposalService.findProposalById(id);

        if (sessionService.sessionExists(proposal)) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Session cannot be created because it already exists."
            ));
        }

        Session newSession = new Session();

        /* Redefine o tempo de expiração da sessão caso receba o parâmetro na requisição */
        if (Objects.nonNull(request) && request.getTimeout() > 0) {
            newSession.setTimeout(request.getTimeout());
        }

        log.info("Session will expires in: {} seconds", newSession.getTimeout());

        proposal.setSession(newSession);

        proposalService.save(proposal);

        log.info("Session created successfully for proposal: {}", id);

        return ResponseEntity.ok().build();
    }

    /**
     * Responsável por criar e persistir informações de votos (yes|no) de uma pauta.
     * Aqui também existe um controle para não permitir votos em uma sessão inválida.
     *
     * @param id identificador da pauta
     * @param request dados de entrada de identificação do votante e sua escolha.
     *
     * @return
     *   Retorno padrão HTTP com base no objeto CustomApiResponse.
     */
    @PostMapping("/{id}/votes")
    public ResponseEntity<CustomApiResponse> newVote(@PathVariable String id, @Valid @RequestBody VoteRequest request) {
        log.info("Received a new vote request ({}) for proposal: {}", request, id);

        Proposal proposal = proposalService.findProposalById(id);

        if (!sessionService.sessionExists(proposal)) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session is not open yet."
            ));
        }

        if (sessionService.sessionClosed(proposal)) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session is closed."
            ));
        }

        if (sessionService.sessionExpired(proposal)) {
            Session session = proposal.getSession();
            session.setEndDate(LocalDateTime.now());
            proposal.setSession(session);
            proposalService.save(proposal);

            return ResponseEntity.badRequest().body(new CustomApiResponse(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Voting session has expired."
            ));
        }

        Vote newVote = new Vote();

        newVote.setProposalId(id);
        newVote.setVoteDate(LocalDateTime.now());
        newVote.setChoice(request.getChoice());
        newVote.setMemberId(request.getMemberId());

        voteService.save(newVote);

        log.info("Vote registered successfully: {}", newVote);

        return ResponseEntity.ok().build();
    }

    /**
     * Responsável por buscar todos os votos efetuados de uma pauta específica.
     *
     * @param id identificador da pauta
     *
     * @return
     *   Retorna uma lista com todos os votos.
     */
    @GetMapping("/{id}/votes")
    public List<Vote> getAllVotes(@PathVariable String id) {
        log.info("Getting all votes for proposal: {}", id);
        return voteService.findByProposalId(id);
    }

    /**
     * Responsável por buscar todos os votos efetuados de uma pauta específica e calcular o total de votos.
     * Contabilização considera somente o primeiro voto de um membro, evitando duplicações.
     *
     * @param id identificador da pauta
     *
     * @return
     *   Retorna o resultado da votação na pauta.
     */
    @GetMapping("/{id}/results")
    public Results getResults(@PathVariable String id) {
        log.info("Received a new results request for proposal: {}", id);
        return voteService.computeVotes(id);
    }
}
