package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.expression.Expression;
import io.szp.expression.ExpressionType;
import io.szp.expression.MultipleTableVariables;
import io.szp.schema.*;

public class DeleteStatement implements Statement {
    private String table_name;
    private Expression expression;

    public DeleteStatement(String table_name, Expression expression) {
        this.table_name = table_name;
        this.expression = expression;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        Table table = database.getTable(table_name);
        MultipleTableVariables variables = new MultipleTableVariables(new Table[] { table });
        ExpressionType type = ExpressionType.BOOL;
        if (expression != null)
            type = expression.checkType(variables);
        int count = 0;
        for (int i = table.getRowSize() - 1; i >= 0; --i) {
            variables.getRows()[0] = i;
            Boolean expression_result;
            if (expression == null || ((expression_result = Expression.convertToBoolean(
                    expression.evaluate(variables), type)) != null && expression_result)) {
                table.removeRow(i);
                ++count;
            }
        }
        table.save();
        Table result = new Table(new Column[] {
                new Column("DELETED", Type.STRING)
        }, "RESULT");
        result.addRow(new Object[] { count });
        return result;
    }
}
