package noah.bompard.coopcycle.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("disponibility", table, columnPrefix + "_disponibility"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));

        columns.add(Column.aliased("restaurant_id", table, columnPrefix + "_restaurant_id"));
        return columns;
    }
}
