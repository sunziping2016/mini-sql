package io.szp.expression;

import io.szp.exception.SQLException;

public class NullConstant implements Expression {
    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return ExpressionType.NULL;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return null;
    }
}
