package noah.bompard.coopcycle.service;

import java.util.List;
import noah.bompard.coopcycle.service.dto.OrderDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link noah.bompard.coopcycle.domain.Order}.
 */
public interface OrderService {
    /**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrderDTO> save(OrderDTO orderDTO);

    /**
     * Updates a order.
     *
     * @param orderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrderDTO> update(OrderDTO orderDTO);

    /**
     * Partially updates a order.
     *
     * @param orderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrderDTO> partialUpdate(OrderDTO orderDTO);

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<OrderDTO> findAll(Pageable pageable);
    /**
     * Get all the OrderDTO where Basket is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<OrderDTO> findAllWhereBasketIsNull();
    /**
     * Get all the OrderDTO where Payment is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<OrderDTO> findAllWherePaymentIsNull();

    /**
     * Returns the number of orders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" order.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrderDTO> findOne(Long id);

    /**
     * Delete the "id" order.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
