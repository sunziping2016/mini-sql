package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.exception.SyntaxException;

public class RealConstant implements Expression {
    private Double real;

    public RealConstant(String raw) {
        try {
            real = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new SyntaxException("Ill formed real literal");
        }
    }

    @Override
    public ExpressionType checkType(Variables variables) throws SQLException {
        return ExpressionType.DOUBLE;
    }

    @Override
    public Object evaluate(Variables variables) throws SQLException {
        return real;
    }
}
