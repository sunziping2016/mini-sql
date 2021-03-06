package io.szp.minisql.statement;

import io.szp.minisql.Database;
import io.szp.minisql.Session;
import io.szp.minisql.Type;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.expression.EmptyVariables;
import io.szp.minisql.expression.Expression;
import io.szp.minisql.expression.ExpressionType;
import io.szp.minisql.expression.Variables;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class InsertStatement implements Statement {
    private String table_name;
    private ArrayList<String> column_list; // can be null
    private ArrayList<ArrayList<Expression>> data;

    public InsertStatement(String table_name, ArrayList<String> column_list, ArrayList<ArrayList<Expression>> data) {
        this.table_name = table_name;
        this.column_list = column_list;
        this.data = data;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        Table table = database.getTable(table_name);
        int column_num = table.getColumnSize();
        int[] column_map;
        Type[] column_type_map;
        if (column_list == null) {
            column_map = new int[column_num];
            for (int i = 0; i < column_num; ++i)
                column_map[i] = i;
        } else {
            column_map = new int[column_list.size()];
            HashMap<String, Integer> column_index = table.getColumnIndex();
            for (int i = 0; i < column_list.size(); ++i) {
                String name = column_list.get(i);
                if (!column_index.containsKey(name))
                    throw new SQLException("Unknown column name");
                column_map[i] = column_index.get(name);
            }
        }
        column_type_map = new Type[column_map.length];
        for (int i = 0; i < column_map.length; ++i)
            column_type_map[i] = table.getColumn(column_map[i]).getType();
        Variables variables = new EmptyVariables();
        int count = 0;
        for (ArrayList<Expression> row : data) {
            if (row.size() != column_map.length)
                throw new SQLException("Row size mismatch");
            Object[] new_row = new Object[column_num];
            for (int i = 0; i < column_map.length; ++i) {
                ExpressionType type = row.get(i).checkType(variables);
                new_row[column_map[i]] = Expression.convertToType(row.get(i).evaluate(variables),
                        type, column_type_map[i]);
            }
            table.addRow(new_row);
            ++count;
        }
        table.save();
        return new Table("RESULT",
                new Column[] { new Column("INSERTED", Type.INT) },
                new Object[][] {
                        new Object[] { count }
                });
    }
}
