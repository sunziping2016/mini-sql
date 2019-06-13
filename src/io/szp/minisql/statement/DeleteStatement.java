package io.szp.minisql.statement;

import io.szp.minisql.Database;
import io.szp.minisql.Session;
import io.szp.minisql.Type;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.expression.Expression;
import io.szp.minisql.expression.ExpressionType;
import io.szp.minisql.expression.MultipleTableVariables;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

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
        return new Table("RESULT",
                new Column[] { new Column("DELETED", Type.INT) },
                new Object[][] {
                        new Object[] { count }
                });
    }
}
