package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.exception.SyntaxException;

public class DecimalConstant implements Expression {
    private Long decimal;

    public DecimalConstant(String raw) {
        try {
            decimal = Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new SyntaxException("Ill formed decimal literal");
        }
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return ExpressionType.LONG;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return decimal;
    }
}
