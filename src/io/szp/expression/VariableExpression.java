package io.szp.expression;

import io.szp.exception.SQLException;

public class VariableExpression implements Expression {
    private String table_name, column_name;

    public VariableExpression(String table_name, String column_name) {
        table_name = table_name;
        column_name = column_name;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return variables.getType(table_name, column_name);
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return variables.get(table_name, column_name);
    }
}
