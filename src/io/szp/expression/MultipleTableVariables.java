package io.szp.expression;

import io.szp.exception.SQLException;
import io.szp.schema.Column;
import io.szp.schema.Table;
import io.szp.schema.Type;

import java.util.HashMap;
import java.util.HashSet;

public class MultipleTableVariables implements Variables {
    private Table[] tables;
    private int[] rows;
    // computed index
    private HashMap<FullColumnName, Position> index;
    private HashSet<FullColumnName> conflicted_index;

    public MultipleTableVariables(Table[] tables) {
        this.tables = tables;
        this.rows = new int[tables.length];

        index = new HashMap<>();
        conflicted_index = new HashSet<>();

        for (int i = 0; i < tables.length; ++i) {
            Table table = tables[i];
            int column_size = table.getColumnSize();
            for (int j = 0; j < column_size; ++j) {
                Column column = table.getColumn(j);
                Position position = new Position(i, j);
                if (table.getName() != null)
                    addColumnNameToIndex(new FullColumnName(table.getName(), column.getName()), position);
                addColumnNameToIndex(new FullColumnName(null, column.getName()), position);
            }
        }
    }

    public int[] getRows() {
        return rows;
    }

    private void addColumnNameToIndex(FullColumnName column_name, Position position) {
        if (conflicted_index.contains(column_name))
            return;
        if (index.containsKey(column_name)) {
            index.remove(column_name);
            conflicted_index.add(column_name);
        } else
            index.put(column_name, position);
    }

    @Override
    public Position getPosition(FullColumnName full_column_name) throws SQLException {
        if (conflicted_index.contains(full_column_name))
            throw new SQLException("Conflicted column name");
        if (index.containsKey(full_column_name))
            return index.get(full_column_name);
        throw new SQLException("Unknown column name");
    }

    @Override
    public ExpressionType getType(Position position) {
        Type type = tables[position.table].getColumn(position.column).getType();
        switch (type) {
            case INT: case LONG:
                return ExpressionType.LONG;
            case FLOAT: case DOUBLE:
                return ExpressionType.DOUBLE;
            case STRING:
                return ExpressionType.STRING;
        }
        throw new RuntimeException("Should not reach here");
    }

    @Override
    public Object getValue(Position position) {
        Table table = tables[position.table];
        Type type = table.getColumn(position.column).getType();
        Object data = table.getData(rows[position.table], position.column);
        if (data == null)
            return data;
        switch (type) {
            case INT:
                return (long) (int) data;
            case FLOAT:
                return (double) (float) data;
            case LONG: case DOUBLE: case STRING:
                return data;
        }
        throw new RuntimeException("Should not reach here");
    }

    public Column getColumn(Position position) {
        return tables[position.table].getColumn(position.column);
    }

    public Object getRawValue(Position position) {
        return tables[position.table].getData(rows[position.table], position.column);
    }
}
