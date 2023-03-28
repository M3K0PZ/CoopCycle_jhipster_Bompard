package noah.bompard.coopcycle.service;

import noah.bompard.coopcycle.service.dto.CooperativeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link noah.bompard.coopcycle.domain.Cooperative}.
 */
public interface CooperativeService {
    /**
     * Save a cooperative.
     *
     * @param cooperativeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CooperativeDTO> save(CooperativeDTO cooperativeDTO);

    /**
     * Updates a cooperative.
     *
     * @param cooperativeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CooperativeDTO> update(CooperativeDTO cooperativeDTO);

    /**
     * Partially updates a cooperative.
     *
     * @param cooperativeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CooperativeDTO> partialUpdate(CooperativeDTO cooperativeDTO);

    /**
     * Get all the cooperatives.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CooperativeDTO> findAll(Pageable pageable);

    /**
     * Get all the cooperatives with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CooperativeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of cooperatives available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" cooperative.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CooperativeDTO> findOne(Long id);

    /**
     * Delete the "id" cooperative.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
