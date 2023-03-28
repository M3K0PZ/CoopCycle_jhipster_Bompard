package noah.bompard.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import noah.bompard.coopcycle.domain.Basket;
import noah.bompard.coopcycle.domain.enumeration.BasketState;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Basket}, with proper type conversions.
 */
@Service
public class BasketRowMapper implements BiFunction<Row, String, Basket> {

    private final ColumnConverter converter;

    public BasketRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Basket} stored in the database.
     */
    @Override
    public Basket apply(Row row, String prefix) {
        Basket entity = new Basket();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBasketId(converter.fromRow(row, prefix + "_basket_id", Long.class));
        entity.setBasketState(converter.fromRow(row, prefix + "_basket_state", BasketState.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
