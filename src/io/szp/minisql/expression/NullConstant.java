package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

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
