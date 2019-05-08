package io.szp.expression;

import io.szp.exception.SQLException;

public class VariableExpression implements Expression {
    private FullColumnName full_column_name;

    public VariableExpression(FullColumnName full_column_name) {
        this.full_column_name = full_column_name;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return variables.getType(full_column_name);
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return variables.get(full_column_name);
    }
}
