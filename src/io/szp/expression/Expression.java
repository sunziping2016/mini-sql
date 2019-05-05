package io.szp.expression;

import io.szp.exception.SQLException;

public interface Expression {
    ExpressionType checkType(Variables variables) throws SQLException;
    Object evaluate(Variables variables) throws SQLException;
}
