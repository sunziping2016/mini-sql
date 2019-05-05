package io.szp.expression;

import io.szp.exception.SQLException;

public class EmptyVariables implements Variables {
    @Override
    public ExpressionType getType(String table_name, String column_name) throws SQLException {
        throw new SQLException("Unknown identifier");
    }

    @Override
    public Object get(String table_name, String column_name) throws SQLException {
        throw new SQLException("Unknown identifier");
    }
}
