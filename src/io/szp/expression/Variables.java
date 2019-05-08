package io.szp.expression;

import io.szp.exception.SQLException;

public interface Variables {
    ExpressionType getType(FullColumnName full_column_name) throws SQLException;
    Object get(FullColumnName full_column_name) throws SQLException;
}
