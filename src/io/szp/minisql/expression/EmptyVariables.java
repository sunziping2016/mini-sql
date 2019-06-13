package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

public class EmptyVariables implements Variables {
    @Override
    public Position getPosition(FullColumnName full_column_name) throws SQLException {
        throw new SQLException("Column name should not be here");
    }

    @Override
    public ExpressionType getType(Position position) {
        throw new RuntimeException("Should not reach here");
    }

    @Override
    public Object getValue(Position position) {
        throw new RuntimeException("Should not reach here");
    }
}
