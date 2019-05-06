package io.szp.expression;

import io.szp.exception.SQLException;

public class IsNullExpression implements Expression {
    private Expression expression;
    private boolean not;

    public IsNullExpression(Expression expression, boolean not) {
        this.expression = expression;
        this.not = not;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        expression.checkType(variables);
        return ExpressionType.BOOL;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        boolean result = expression.evaluate(variables) == null;
        if (not)
            return !result;
        return result;
    }
}
