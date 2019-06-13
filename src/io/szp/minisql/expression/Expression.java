package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.Type;

public interface Expression {
    ExpressionType checkType(Variables variables) throws SQLException;
    Object evaluate(Variables variables) throws SQLException;

    static Boolean convertToBoolean(Object object, ExpressionType type) {
        if (object == null)
            return null;
        switch (type) {
            case BOOL:
                return (Boolean) object;
            case LONG:
                return (Long) object != 0;
            case DOUBLE:
                return (Double) object != 0.0;
            case STRING:
                return !((String) object).isEmpty();
        }
        throw new RuntimeException("Should not reach here");
    }

    static Object convertToType(Object object, ExpressionType type, Type target_type) throws SQLException {
        if (object == null)
            return null;
        switch (target_type) {
            case INT:
                switch (type) {
                    case LONG:
                        return (int) (long) object;
                    case DOUBLE:
                        return (int) (double) object;
                    default:
                        throw new SQLException("Cannot convert to int expression");
                }
            case LONG:
                switch (type) {
                    case LONG:
                        return object;
                    case DOUBLE:
                        return (long) (double) object;
                    default:
                        throw new SQLException("Cannot convert to long expression");
                }
            case FLOAT:
                switch (type) {
                    case LONG:
                        return (float) (long) object;
                    case DOUBLE:
                        return (float) (double) object;
                    default:
                        throw new SQLException("Cannot convert to float expression");
                }
            case DOUBLE:
                switch (type) {
                    case LONG:
                        return (double) (long) object;
                    case DOUBLE:
                        return object;
                    default:
                        throw new SQLException("Cannot convert to double expression");
                }
            case STRING:
                if (type == ExpressionType.BOOL)
                    throw new SQLException("Cannot convert string bool to string");
                else
                    return object.toString();
        }
        throw new RuntimeException("Should not reach here");
    }
}
