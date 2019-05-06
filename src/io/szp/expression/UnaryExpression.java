package io.szp.expression;

import io.szp.exception.SQLException;

public class UnaryExpression implements Expression {
    public enum Operator {
        NOT,
        POSITIVE,
        NEGATIVE
    }

    private Operator operator;
    private Expression expression;

    private ExpressionType type;

    public UnaryExpression(Operator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        type = expression.checkType(variables);
        if (operator == Operator.NOT)
            return ExpressionType.BOOL;
        else if (type != ExpressionType.LONG && type != ExpressionType.DOUBLE)
            throw new SQLException("Unary + or - requires numeric values");
        return type;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        Object result = expression.evaluate(variables);
        if (result == null)
            return null;
        switch (operator) {
            case NOT:
                return !Expression.convertToBoolean(result, type);
            case POSITIVE:
                return result;
            case NEGATIVE:
                if (type == ExpressionType.LONG)
                    return -(Long) result;
                else
                    return -(Double) result;
        }
        // should not reach here
        return null;
    }
}
