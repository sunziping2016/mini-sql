package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.expression.EmptyVariables;
import io.szp.expression.Expression;
import io.szp.expression.Variables;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

public class SelectStatement implements Statement {
    private Expression expression;

    // TODO
    public SelectStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Variables variables = new EmptyVariables();
        System.out.println(expression.checkType(variables));
        System.out.println(expression.evaluate(variables));
        return null;
    }
}
