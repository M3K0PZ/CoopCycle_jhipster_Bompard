package noah.bompard.coopcycle.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import noah.bompard.coopcycle.domain.Basket;
import noah.bompard.coopcycle.domain.enumeration.BasketState;
import noah.bompard.coopcycle.repository.rowmapper.BasketRowMapper;
import noah.bompard.coopcycle.repository.rowmapper.OrderRowMapper;
import noah.bompard.coopcycle.repository.rowmapper.UserRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Basket entity.
 */
@SuppressWarnings("unused")
class BasketRepositoryInternalImpl extends SimpleR2dbcRepository<Basket, Long> implements BasketRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final OrderRowMapper orderMapper;
    private final BasketRowMapper basketMapper;

    private static final Table entityTable = Table.aliased("basket", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");
    private static final Table customerTable = Table.aliased("jhi_user", "customer");

    public BasketRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        OrderRowMapper orderMapper,
        BasketRowMapper basketMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Basket.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.basketMapper = basketMapper;
    }

    @Override
    public Flux<Basket> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Basket> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BasketSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(UserSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Basket.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Basket> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Basket> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Basket process(Row row, RowMetadata metadata) {
        Basket entity = basketMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setOrder(orderMapper.apply(row, "order"));
        entity.setCustomer(userMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Basket> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
