package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

public class BooleanConstant implements Expression {
    private Boolean value;

    public BooleanConstant(Boolean value) {
        this.value = value;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return ExpressionType.BOOL;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return value;
    }
}
