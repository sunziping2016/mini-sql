package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.expression.Expression;
import io.szp.expression.ExpressionType;
import io.szp.expression.MultipleTableVariables;
import io.szp.schema.Column;
import io.szp.schema.Database;
import io.szp.schema.Table;

import java.util.ArrayList;

public class TableSource {
    public static class JoinPart {
        public String name;
        public Expression expression;

        public JoinPart(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }
    }

    private String base_name;
    private ArrayList<JoinPart> joins_parts;
    private String alias;

    public TableSource(String base_name, ArrayList<JoinPart> joins_parts, String alias) {
        this.base_name = base_name;
        this.joins_parts = joins_parts;
        this.alias = alias;
    }

    public Table execute(Database database) throws SQLException {
        Table base = database.getTable(base_name);
        if (joins_parts.isEmpty()) {
            if (alias != null && !alias.equals(base.getName())) {
                Table copy = new Table(alias, base.getColumns());
                for (Object[] row : base.getData())
                    copy.addRow(row);
                base = copy;
            }
        } else {
            for (JoinPart join : joins_parts) {
                Table other = database.getTable(join.name);
                MultipleTableVariables variables = new MultipleTableVariables(new Table[] {
                        base,
                        other
                });
                Column[] result_columns = new Column[base.getColumnSize() + other.getColumnSize()];
                System.arraycopy(base.getColumns(), 0,
                        result_columns, 0,  base.getColumnSize());
                System.arraycopy(other.getColumns(), 0,
                        result_columns, base.getColumnSize(), other.getColumnSize());
                Table result = new Table(base.getName() + "JOIN" + other.getName(), result_columns);
                ExpressionType type = ExpressionType.BOOL;
                if (join.expression != null)
                    type = join.expression.checkType(variables);
                for (int i = 0; i < base.getRowSize(); ++i) {
                    for (int j = 0; j < other.getRowSize(); ++j) {
                        int[] variable_rows = variables.getRows();
                        variable_rows[0] = i;
                        variable_rows[1] = j;
                        Boolean expression_result;
                        if (join.expression == null || ((expression_result = Expression.convertToBoolean(
                                join.expression.evaluate(variables), type)) != null && expression_result)) {
                            Object[] data = new Object[base.getColumnSize() + other.getColumnSize()];
                            System.arraycopy(base.getData(i), 0,
                                    data, 0, base.getColumnSize());
                            System.arraycopy(other.getData(j), 0,
                                    data, base.getColumnSize(), other.getColumnSize());
                            result.addRow(data);
                        }
                    }
                }
                base = result;
            }
            if (alias != null)
                base.setName(alias);
        }
        return base;
    }
}
