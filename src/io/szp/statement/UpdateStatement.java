package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.expression.*;
import io.szp.schema.*;

import java.util.ArrayList;

public class UpdateStatement implements Statement {
    public static class UpdatedElement {
        public String column_name;
        public Expression expression;

        public UpdatedElement(String column_name, Expression expression) {
            this.column_name = column_name;
            this.expression = expression;
        }
    }

    private String table_name;
    private ArrayList<UpdatedElement> updated_elements;
    private Expression expression;

    public UpdateStatement(String table_name,
                           ArrayList<UpdatedElement> updated_elements,
                           Expression expression) {
        this.table_name = table_name;
        this.updated_elements = updated_elements;
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
        ArrayList<Variables.Position> positions = new ArrayList<>();
        ArrayList<ExpressionType> expression_types = new ArrayList<>();
        ArrayList<Type> target_types = new ArrayList<Type>();
        for (UpdatedElement updated_element : updated_elements) {
            Variables.Position position = variables.getPosition(
                    new FullColumnName(null, updated_element.column_name));
            positions.add(position);
            expression_types.add(updated_element.expression.checkType(variables));
            target_types.add(variables.getColumn(position).getType());
        }
        if (expression != null)
            type = expression.checkType(variables);
        int count = 0;
        for (int i = 0; i < table.getRowSize(); ++i) {
            variables.getRows()[0] = i;
            Boolean expression_result;
            if (expression == null || ((expression_result = Expression.convertToBoolean(
                    expression.evaluate(variables), type)) != null && expression_result)) {
                Object[] old_row = table.getData(i);
                for (int j = 0; j < updated_elements.size(); ++j) {
                    UpdatedElement updated_element = updated_elements.get(j);
                    old_row[positions.get(j).column] = Expression.convertToType(
                            updated_element.expression.evaluate(variables),
                            expression_types.get(j), target_types.get(j));
                }
                ++count;
            }
        }
        table.save();
        Table result = new Table(new Column[] {
                new Column("UPDATED", Type.STRING)
        }, "RESULT");
        result.addRow(new Object[] { count });
        return result;
    }
}
