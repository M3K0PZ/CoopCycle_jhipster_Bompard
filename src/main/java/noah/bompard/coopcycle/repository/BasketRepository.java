package noah.bompard.coopcycle.repository;

import noah.bompard.coopcycle.domain.Basket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Basket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BasketRepository extends ReactiveCrudRepository<Basket, Long>, BasketRepositoryInternal {
    Flux<Basket> findAllBy(Pageable pageable);

    @Query("SELECT * FROM basket entity WHERE entity.user_id = :id")
    Flux<Basket> findByUser(Long id);

    @Query("SELECT * FROM basket entity WHERE entity.user_id IS NULL")
    Flux<Basket> findAllWhereUserIsNull();

    @Query("SELECT * FROM basket entity WHERE entity.order_id = :id")
    Flux<Basket> findByOrder(Long id);

    @Query("SELECT * FROM basket entity WHERE entity.order_id IS NULL")
    Flux<Basket> findAllWhereOrderIsNull();

    @Query("SELECT * FROM basket entity WHERE entity.customer_id = :id")
    Flux<Basket> findByCustomer(Long id);

    @Query("SELECT * FROM basket entity WHERE entity.customer_id IS NULL")
    Flux<Basket> findAllWhereCustomerIsNull();

    @Override
    <S extends Basket> Mono<S> save(S entity);

    @Override
    Flux<Basket> findAll();

    @Override
    Mono<Basket> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BasketRepositoryInternal {
    <S extends Basket> Mono<S> save(S entity);

    Flux<Basket> findAllBy(Pageable pageable);

    Flux<Basket> findAll();

    Mono<Basket> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Basket> findAllBy(Pageable pageable, Criteria criteria);

}
