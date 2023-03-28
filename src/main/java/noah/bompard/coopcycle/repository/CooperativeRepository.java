package noah.bompard.coopcycle.repository;

import noah.bompard.coopcycle.domain.Cooperative;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Cooperative entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CooperativeRepository extends ReactiveCrudRepository<Cooperative, Long>, CooperativeRepositoryInternal {
    Flux<Cooperative> findAllBy(Pageable pageable);

    @Override
    Mono<Cooperative> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Cooperative> findAllWithEagerRelationships();

    @Override
    Flux<Cooperative> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM cooperative entity JOIN rel_cooperative__restaurant joinTable ON entity.id = joinTable.restaurant_id WHERE joinTable.restaurant_id = :id"
    )
    Flux<Cooperative> findByRestaurant(Long id);

    @Override
    <S extends Cooperative> Mono<S> save(S entity);

    @Override
    Flux<Cooperative> findAll();

    @Override
    Mono<Cooperative> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CooperativeRepositoryInternal {
    <S extends Cooperative> Mono<S> save(S entity);

    Flux<Cooperative> findAllBy(Pageable pageable);

    Flux<Cooperative> findAll();

    Mono<Cooperative> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Cooperative> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Cooperative> findOneWithEagerRelationships(Long id);

    Flux<Cooperative> findAllWithEagerRelationships();

    Flux<Cooperative> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
