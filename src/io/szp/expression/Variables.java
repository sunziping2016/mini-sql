package io.szp.expression;

import io.szp.exception.SQLException;

interface Variables {
    ExpressionType getType(String table_name, String column_name) throws SQLException;
    Object get(String table_name, String column_name) throws SQLException;
}