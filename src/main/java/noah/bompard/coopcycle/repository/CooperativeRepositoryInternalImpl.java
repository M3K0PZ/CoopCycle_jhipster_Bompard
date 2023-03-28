package noah.bompard.coopcycle.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import noah.bompard.coopcycle.domain.Cooperative;
import noah.bompard.coopcycle.domain.Restaurant;
import noah.bompard.coopcycle.repository.rowmapper.CooperativeRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Cooperative entity.
 */
@SuppressWarnings("unused")
class CooperativeRepositoryInternalImpl extends SimpleR2dbcRepository<Cooperative, Long> implements CooperativeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CooperativeRowMapper cooperativeMapper;

    private static final Table entityTable = Table.aliased("cooperative", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable restaurantLink = new EntityManager.LinkTable(
        "rel_cooperative__restaurant",
        "cooperative_id",
        "restaurant_id"
    );

    public CooperativeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CooperativeRowMapper cooperativeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Cooperative.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cooperativeMapper = cooperativeMapper;
    }

    @Override
    public Flux<Cooperative> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Cooperative> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CooperativeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Cooperative.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Cooperative> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Cooperative> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Cooperative> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Cooperative> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Cooperative> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Cooperative process(Row row, RowMetadata metadata) {
        Cooperative entity = cooperativeMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Cooperative> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Cooperative> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(restaurantLink, entity.getId(), entity.getRestaurants().stream().map(Restaurant::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(restaurantLink, entityId);
    }
}
