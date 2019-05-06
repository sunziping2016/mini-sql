package io.szp.expression;

import io.szp.exception.SQLException;

import java.util.function.DoublePredicate;

public class CompareExpression implements Expression {
    public enum Operator {
        GREAT_THAN,
        LESS_THAN,
        GREAT_EQUAL,
        LESS_EQUAL,
        EQUAL,
        NOT_EQUAL
    }

    private Operator operator;
    private Expression left, right;
    private ExpressionType left_type, right_type;

    public CompareExpression(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        left_type = left.checkType(variables);
        right_type = right.checkType(variables);
        switch (operator) {
            case GREAT_THAN: case LESS_THAN:
            case GREAT_EQUAL: case LESS_EQUAL:
                if ((left_type != ExpressionType.LONG && left_type != ExpressionType.DOUBLE) ||
                        (right_type != ExpressionType.LONG && right_type != ExpressionType.DOUBLE))
                    throw new SQLException("Comparison expression requires numeric values");
                break;
            case NOT_EQUAL: case EQUAL:
                // We don't know whether `"false" = false` should be true or not
                // So we forbid it
                if ((left_type == ExpressionType.BOOL && right_type == ExpressionType.STRING) ||
                        (left_type == ExpressionType.STRING && right_type == ExpressionType.BOOL))
                    throw new SQLException("It\'s ambiguous to decide the equality of string and boolean");

        }
        return ExpressionType.BOOL;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Object evaluate(Variables variables) throws SQLException {
        Object left_raw_value = left.evaluate(variables), right_raw_value = right.evaluate(variables);
        if (left_raw_value == null || right_raw_value == null)
            return null;
        // I don't know how to simplify the following code
        switch (left_type) {
            case BOOL: {
                Boolean left_value = (Boolean) left_raw_value;
                switch (right_type) {
                    case BOOL: {
                        Boolean right_value = (Boolean) right_raw_value;
                        if (operator == Operator.EQUAL)
                            return left_value == right_value;
                        else
                            return left_value != right_value;
                    }
                    case LONG: {
                        Boolean right_value = Expression.convertToBoolean(right_raw_value, ExpressionType.LONG);
                        if (operator == Operator.EQUAL)
                            return left_value == right_value;
                        else
                            return left_value != right_value;
                    }
                    case DOUBLE: {
                        Boolean right_value = Expression.convertToBoolean(right_raw_value, ExpressionType.DOUBLE);
                        if (operator == Operator.EQUAL)
                            return left_value == right_value;
                        else
                            return left_value != right_value;
                    }
                }
                throw new RuntimeException("Should not reach here");
            }
            case STRING: {
                String left_value = (String) left_raw_value;
                switch (right_type) {
                    case STRING: {
                        String right_value = (String) right_raw_value;
                        if (operator == Operator.EQUAL)
                            return left_value.equals(right_value);
                        else
                            return !left_value.equals(right_value);
                    }
                    case LONG: {
                        try {
                            Long left_parsed_value = Long.parseLong(left_value);
                            Long right_value = (Long) right_raw_value;
                            if (operator == Operator.EQUAL)
                                return left_parsed_value.equals(right_value);
                            else
                                return !left_parsed_value.equals(right_value);
                        } catch (NumberFormatException e) {
                            return operator != Operator.EQUAL;
                        }
                    }
                    case DOUBLE: {
                        try {
                            Double left_parsed_value = Double.parseDouble(left_value);
                            Double right_value = (Double) right_raw_value;
                            if (operator == Operator.EQUAL)
                                return left_parsed_value.equals(right_value);
                            else
                                return !left_parsed_value.equals(right_value);
                        } catch (NumberFormatException e) {
                            return operator != Operator.EQUAL;
                        }
                    }
                }
                throw new RuntimeException("Should not reach here");
            }
            case LONG: {
                Long left_value = (Long) left_raw_value;
                switch (right_type) {
                    case BOOL: {
                        Boolean left_parsed_value = Expression.convertToBoolean(left_raw_value, ExpressionType.LONG);
                        Boolean right_value = (Boolean) right_raw_value;
                        if (operator == Operator.EQUAL)
                            return left_parsed_value == right_value;
                        else
                            return left_parsed_value != right_value;
                    }
                    case STRING: {
                        try {
                            Long right_parsed_value = Long.parseLong((String) right_raw_value);
                            if (operator == Operator.EQUAL)
                                return left_value.equals(right_parsed_value);
                            else
                                return !left_value.equals(right_parsed_value);
                        } catch (NumberFormatException e) {
                            return operator != Operator.EQUAL;
                        }
                    }
                    case LONG: {
                        Long right_value = (Long) right_raw_value;
                        switch (operator) {
                            case EQUAL:
                                return left_value.equals(right_value);
                            case NOT_EQUAL:
                                return !left_value.equals(right_value);
                            case LESS_THAN:
                                return left_value < right_value;
                            case GREAT_THAN:
                                return left_value > right_value;
                            case LESS_EQUAL:
                                return left_value <= right_value;
                            case GREAT_EQUAL:
                                return left_value >= right_value;
                        }
                    }
                    case DOUBLE: {
                        Double right_value = (Double) right_raw_value;
                        switch (operator) {
                            case EQUAL:
                                return (long) left_value == right_value;
                            case NOT_EQUAL:
                                return (long) left_value != right_value;
                            case LESS_THAN:
                                return left_value < right_value;
                            case GREAT_THAN:
                                return left_value > right_value;
                            case LESS_EQUAL:
                                return left_value <= right_value;
                            case GREAT_EQUAL:
                                return left_value >= right_value;
                        }
                    }
                }
                throw new RuntimeException("Should not reach here");
            }
            case DOUBLE: {
                Double left_value = (Double) left_raw_value;
                switch (right_type) {
                    case BOOL: {
                        Boolean left_parsed_value = Expression.convertToBoolean(left_raw_value, ExpressionType.DOUBLE);
                        Boolean right_value = (Boolean) right_raw_value;
                        if (operator == Operator.EQUAL)
                            return left_parsed_value == right_value;
                        else
                            return left_parsed_value != right_value;
                    }
                    case STRING: {
                        try {
                            Double right_parsed_value = Double.parseDouble((String) right_raw_value);
                            if (operator == Operator.EQUAL)
                                return left_value.equals(right_parsed_value);
                            else
                                return !left_value.equals(right_parsed_value);
                        } catch (NumberFormatException e) {
                            return operator != Operator.EQUAL;
                        }
                    }
                    case LONG: {
                        Long right_value = (Long) right_raw_value;
                        switch (operator) {
                            case EQUAL:
                                return (double) left_value == right_value;
                            case NOT_EQUAL:
                                return (double) left_value != right_value;
                            case LESS_THAN:
                                return left_value < right_value;
                            case GREAT_THAN:
                                return left_value > right_value;
                            case LESS_EQUAL:
                                return left_value <= right_value;
                            case GREAT_EQUAL:
                                return left_value >= right_value;
                        }
                    }
                    case DOUBLE: {
                        Double right_value = (Double) right_raw_value;
                        switch (operator) {
                            case EQUAL:
                                return left_value.equals(right_value);
                            case NOT_EQUAL:
                                return !left_value.equals(right_value);
                            case LESS_THAN:
                                return left_value < right_value;
                            case GREAT_THAN:
                                return left_value > right_value;
                            case LESS_EQUAL:
                                return left_value <= right_value;
                            case GREAT_EQUAL:
                                return left_value >= right_value;
                        }
                    }
                }
                throw new RuntimeException("Should not reach here");
            }
        }
        throw new RuntimeException("Should not reach here");
    }
}
