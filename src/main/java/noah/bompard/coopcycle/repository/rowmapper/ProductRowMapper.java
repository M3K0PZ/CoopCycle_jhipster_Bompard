package noah.bompard.coopcycle.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import noah.bompard.coopcycle.domain.Product;
import noah.bompard.coopcycle.domain.enumeration.Disponibility;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setDisponibility(converter.fromRow(row, prefix + "_disponibility", Disponibility.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", Long.class));
        return entity;
    }
}
