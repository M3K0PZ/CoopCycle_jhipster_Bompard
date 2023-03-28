package noah.bompard.coopcycle.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import noah.bompard.coopcycle.repository.BasketRepository;
import noah.bompard.coopcycle.service.BasketService;
import noah.bompard.coopcycle.service.dto.BasketDTO;
import noah.bompard.coopcycle.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link noah.bompard.coopcycle.domain.Basket}.
 */
@RestController
@RequestMapping("/api")
public class BasketResource {

    private final Logger log = LoggerFactory.getLogger(BasketResource.class);

    private static final String ENTITY_NAME = "basket";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BasketService basketService;

    private final BasketRepository basketRepository;

    public BasketResource(BasketService basketService, BasketRepository basketRepository) {
        this.basketService = basketService;
        this.basketRepository = basketRepository;
    }

    /**
     * {@code POST  /baskets} : Create a new basket.
     *
     * @param basketDTO the basketDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new basketDTO, or with status {@code 400 (Bad Request)} if the basket has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/baskets")
    public Mono<ResponseEntity<BasketDTO>> createBasket(@Valid @RequestBody BasketDTO basketDTO) throws URISyntaxException {
        log.debug("REST request to save Basket : {}", basketDTO);
        if (basketDTO.getId() != null) {
            throw new BadRequestAlertException("A new basket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return basketService
            .save(basketDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/baskets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /baskets/:id} : Updates an existing basket.
     *
     * @param id the id of the basketDTO to save.
     * @param basketDTO the basketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketDTO,
     * or with status {@code 400 (Bad Request)} if the basketDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the basketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/baskets/{id}")
    public Mono<ResponseEntity<BasketDTO>> updateBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BasketDTO basketDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Basket : {}, {}", id, basketDTO);
        if (basketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return basketRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return basketService
                    .update(basketDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /baskets/:id} : Partial updates given fields of an existing basket, field will ignore if it is null
     *
     * @param id the id of the basketDTO to save.
     * @param basketDTO the basketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketDTO,
     * or with status {@code 400 (Bad Request)} if the basketDTO is not valid,
     * or with status {@code 404 (Not Found)} if the basketDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the basketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/baskets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BasketDTO>> partialUpdateBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BasketDTO basketDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Basket partially : {}, {}", id, basketDTO);
        if (basketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return basketRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BasketDTO> result = basketService.partialUpdate(basketDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /baskets} : get all the baskets.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of baskets in body.
     */
    @GetMapping("/baskets")
    public Mono<ResponseEntity<List<BasketDTO>>> getAllBaskets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Baskets");
        return basketService
            .countAll()
            .zipWith(basketService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /baskets/:id} : get the "id" basket.
     *
     * @param id the id of the basketDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the basketDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/baskets/{id}")
    public Mono<ResponseEntity<BasketDTO>> getBasket(@PathVariable Long id) {
        log.debug("REST request to get Basket : {}", id);
        Mono<BasketDTO> basketDTO = basketService.findOne(id);
        return ResponseUtil.wrapOrNotFound(basketDTO);
    }

    /**
     * {@code DELETE  /baskets/:id} : delete the "id" basket.
     *
     * @param id the id of the basketDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/baskets/{id}")
    public Mono<ResponseEntity<Void>> deleteBasket(@PathVariable Long id) {
        log.debug("REST request to delete Basket : {}", id);
        return basketService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
