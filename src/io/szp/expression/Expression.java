package io.szp.expression;

import io.szp.exception.SQLException;

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
}
