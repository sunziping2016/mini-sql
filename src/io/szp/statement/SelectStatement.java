package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.expression.*;
import io.szp.schema.*;

import java.util.ArrayList;

public class SelectStatement implements Statement {
    public static class SelectElement {
        public FullColumnName column_name;
        public String alias;

        public SelectElement(FullColumnName column_name, String alias) {
            this.column_name = column_name;
            this.alias = alias;
        }
    }

    private ArrayList<SelectElement> select_elements;
    private ArrayList<TableSource> table_sources;
    private Expression expression;

    public SelectStatement(ArrayList<SelectElement> select_elements,
                           ArrayList<TableSource> table_sources,
                           Expression expression) {
        this.select_elements = select_elements;
        this.table_sources = table_sources;
        this.expression = expression;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        ArrayList<Table> tables = new ArrayList<>();
        for (TableSource table_source : table_sources)
            tables.add(table_source.execute(database));
        MultipleTableVariables variables = new MultipleTableVariables(tables.toArray(new Table[0]));
        ExpressionType type = ExpressionType.BOOL;
        if (expression != null)
            type = expression.checkType(variables);
        ArrayList<Variables.Position> positions = new ArrayList<>();
        if (select_elements == null) {
            for (int i = 0; i < tables.size(); ++i) {
                Table table = tables.get(i);
                for (int j = 0; j < table.getColumnSize(); ++j)
                    positions.add(new Variables.Position(i, j));
            }
        } else {
            for (SelectElement select_element : select_elements)
                positions.add(variables.getPosition(select_element.column_name));
        }
        ArrayList<Column> columns = new ArrayList<>();
        for (Variables.Position position : positions)
            columns.add(variables.getColumn(position)); // TODO
        Table result = new Table(columns.toArray(new Column[0]), "result");
        cartesianProduct(tables, variables, expression, type, positions, result, 0);
        if (select_elements != null) {
            for (int i = 0; i < select_elements.size(); ++i) {
                SelectElement select_element = select_elements.get(i);
                if (select_element.alias != null)
                    result.getColumn(i).setName(select_element.alias);
            }
        }
        return result;
    }

    private static void cartesianProduct(ArrayList<Table> tables,
                                         MultipleTableVariables variables,
                                         Expression expression,
                                         ExpressionType type,
                                         ArrayList<Variables.Position> positions,
                                         Table result,
                                         int table_index) throws SQLException {
        if (table_index == tables.size()) {
            Boolean expression_result;
            if (expression == null || ((expression_result = Expression.convertToBoolean(
                    expression.evaluate(variables), type)) != null && expression_result)) {
                Object[] data = new Object[positions.size()];
                for (int i = 0; i < positions.size(); ++i) {
                    Variables.Position position = positions.get(i);
                    data[i] = variables.getRawValue(position);
                }
                result.addRow(data);
            }
        } else {
            for (int i = 0; i < tables.get(table_index).getRowSize(); ++i) {
                variables.getRows()[table_index] = i;
                cartesianProduct(tables, variables, expression, type,
                        positions, result, table_index + 1);
            }
        }
    }
}
