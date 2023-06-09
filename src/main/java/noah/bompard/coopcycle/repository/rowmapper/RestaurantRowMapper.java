package noah.bompard.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import noah.bompard.coopcycle.domain.Restaurant;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Restaurant}, with proper type conversions.
 */
@Service
public class RestaurantRowMapper implements BiFunction<Row, String, Restaurant> {

    private final ColumnConverter converter;

    public RestaurantRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Restaurant} stored in the database.
     */
    @Override
    public Restaurant apply(Row row, String prefix) {
        Restaurant entity = new Restaurant();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
