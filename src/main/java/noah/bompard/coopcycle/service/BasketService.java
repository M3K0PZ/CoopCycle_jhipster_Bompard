package noah.bompard.coopcycle.service;

import noah.bompard.coopcycle.service.dto.BasketDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link noah.bompard.coopcycle.domain.Basket}.
 */
public interface BasketService {
    /**
     * Save a basket.
     *
     * @param basketDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<BasketDTO> save(BasketDTO basketDTO);

    /**
     * Updates a basket.
     *
     * @param basketDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<BasketDTO> update(BasketDTO basketDTO);

    /**
     * Partially updates a basket.
     *
     * @param basketDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<BasketDTO> partialUpdate(BasketDTO basketDTO);

    /**
     * Get all the baskets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<BasketDTO> findAll(Pageable pageable);

    /**
     * Returns the number of baskets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" basket.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<BasketDTO> findOne(Long id);

    /**
     * Delete the "id" basket.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
