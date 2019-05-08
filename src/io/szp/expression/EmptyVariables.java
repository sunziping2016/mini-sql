package io.szp.expression;

import io.szp.exception.SQLException;

public class EmptyVariables implements Variables {
    @Override
    public ExpressionType getType(FullColumnName full_column_name) throws SQLException {
        throw new SQLException("Unknown identifier");
    }

    @Override
    public Object get(FullColumnName full_column_name) throws SQLException {
        throw new SQLException("Unknown identifier");
    }
}
