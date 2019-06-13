package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

public class VariableExpression implements Expression {
    private FullColumnName full_column_name;
    private Variables.Position position;

    public VariableExpression(FullColumnName full_column_name) {
        this.full_column_name = full_column_name;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        position = variables.getPosition(full_column_name);
        return variables.getType(position);
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return variables.getValue(position);
    }
}
