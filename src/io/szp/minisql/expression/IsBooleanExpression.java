package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

public class IsBooleanExpression implements Expression {
    private Expression expression;
    private Boolean bool;
    private boolean not;
    private ExpressionType type;

    public IsBooleanExpression(Expression expression, Boolean bool, boolean not) {
        this.expression = expression;
        this.bool = bool;
        this.not = not;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        type = expression.checkType(variables);
        return ExpressionType.BOOL;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        Boolean predict = Expression.convertToBoolean(expression.evaluate(variables), type);
        boolean result;
        if (bool == null) {
            result = predict == null;
        } else if (predict == null)
            result = false;
        else
            result = bool == predict;
        if (not)
            return !result;
        else
            return result;
    }
}
