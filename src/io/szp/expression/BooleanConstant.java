package io.szp.expression;

import io.szp.exception.SQLException;

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
