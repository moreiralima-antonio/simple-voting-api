package br.com.antoniolima.simplevotingapi.controller;

import org.springframework.web.bind.annotation.RestController;

import br.com.antoniolima.simplevotingapi.domain.Proposal;
import br.com.antoniolima.simplevotingapi.domain.in.ProposalRequest;
import br.com.antoniolima.simplevotingapi.domain.out.ProposalResponse;
import br.com.antoniolima.simplevotingapi.exception.CustomApiException;
import br.com.antoniolima.simplevotingapi.domain.Results;
import br.com.antoniolima.simplevotingapi.domain.in.SessionRequest;
import br.com.antoniolima.simplevotingapi.domain.Vote;
import br.com.antoniolima.simplevotingapi.domain.in.VoteRequest;
import br.com.antoniolima.simplevotingapi.service.ProposalService;
import br.com.antoniolima.simplevotingapi.service.SessionService;
import br.com.antoniolima.simplevotingapi.service.VoteService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.validation.Valid;

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

    private final VoteService voteService;
    private final SessionService sessionService;
    private final ProposalService proposalService;

    public ProposalController(final SessionService sessionService, final ProposalService proposalService,
        final VoteService voteService) {

        this.sessionService = sessionService;
        this.proposalService = proposalService;
        this.voteService = voteService;
    }

    /**
     * Responsável por criar e persistir informações sobre uma pauta.
     *
     * @param request dados de entrada contendo a descrição da pauta.
     *
     * @return Retorna um identificador único da pauta criada.
     */
    @PostMapping
    public ResponseEntity<ProposalResponse> newProposal(@Valid @RequestBody ProposalRequest request) {
        log.info("Received a new proposal request with data: {}", request);

        Proposal newProposal = new Proposal(request.getDescription());
        Proposal savedProposal = proposalService.save(newProposal);

        log.info("Proposal created with id: {}", savedProposal.getId());

        return new ResponseEntity<>(new ProposalResponse(savedProposal.getId()), HttpStatus.CREATED);
    }

    /**
     * Responsável por criar e persistir informações sobre uma sessão dentro de uma
     * pauta previamente criada. Aqui também é realizado um controle para não
     * permitir abertura de mais de uma sessão em uma mesma pauta.
     *
     * @param id      identificador da pauta
     * @param request dados de entrada contendo o tempo de expiração (segundos) da
     *                sessão de votação (opcional).
     *
     * @return Retorno padrão HTTP com base no objeto CustomApiResponse.
     * @throws CustomApiException
     */
    @PostMapping("/{id}/sessions")
    public ResponseEntity<Void> newSession(@PathVariable String id,
            @Valid @RequestBody(required = false) SessionRequest request) throws CustomApiException {

        log.info("Received a new session request for proposal: {}", id);

        sessionService.newSession(id, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Responsável por criar e persistir informações de votos (yes|no) de uma pauta.
     * Aqui também existe um controle para não permitir votos em uma sessão
     * inválida.
     *
     * @param id      identificador da pauta
     * @param request dados de entrada de identificação do votante e sua escolha.
     *
     * @return Retorno padrão HTTP com base no objeto CustomApiResponse.
     * @throws CustomApiException
     */
    @PostMapping("/{id}/votes")
    public ResponseEntity<Void> newVote(@PathVariable String id,
            @Valid @RequestBody VoteRequest request) throws CustomApiException {

        log.info("Received a new vote request ({}) for proposal: {}", request, id);

        voteService.newVote(id, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Responsável por buscar todos os votos efetuados de uma pauta específica.
     *
     * @param id identificador da pauta
     *
     * @return Retorna uma lista com todos os votos.
     */
    @GetMapping("/{id}/votes")
    public ResponseEntity<List<Vote>> getAllVotes(@PathVariable String id) {

        log.info("Getting all votes for proposal: {}", id);

        return new ResponseEntity<>(voteService.findByProposalId(id), HttpStatus.OK);
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
    public ResponseEntity<Results> getResults(@PathVariable String id) {

        log.info("Received a new results request for proposal: {}", id);

        return new ResponseEntity<>(voteService.computeVotes(id), HttpStatus.OK);
    }
}
