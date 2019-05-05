package io.szp.expression;

import io.szp.exception.SQLException;
import io.szp.exception.SyntaxException;

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
