package io.szp.expression;

import io.szp.exception.SQLException;

public class LogicalExpression implements Expression {
    public enum Operator {
        AND,
        OR
    }

    private Expression left, right;
    private Operator operator;
    private ExpressionType left_type, right_type;

    public LogicalExpression(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        left_type = left.checkType(variables);
        right_type = right.checkType(variables);
        return ExpressionType.BOOL;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        Boolean left_value = Expression.convertToBoolean(left.evaluate(variables), left_type);
        Boolean right_value = Expression.convertToBoolean(right.evaluate(variables), right_type);
        switch (operator) {
            case AND:
                if (left_value == null) {
                    if (right_value == null || right_value)
                        return null;
                    else
                        return false;
                } else if (left_value)
                    return right_value;
                else
                    return false;
            case OR:
                if (left_value == null) {
                    if (right_value == null || !right_value)
                        return null;
                    else
                        return true;
                } else if (left_value)
                    return true;
                else
                    return right_value;
        }
        throw new RuntimeException("Should not reach here");
    }
}
