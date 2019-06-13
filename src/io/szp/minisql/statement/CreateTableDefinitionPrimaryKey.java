package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;

import java.util.ArrayList;

public class CreateTableDefinitionPrimaryKey implements CreateTableDefinition {
    private ArrayList<String> names;

    public CreateTableDefinitionPrimaryKey(ArrayList<String> names) {
        this.names = names;
    }

    @Override
    public void apply(ArrayList<Column> columns) throws SQLException {
        for (String name : names) {
            boolean found = false;
            for (Column column : columns) {
                if (column.getName().equals(name)) {
                    found = true;
                    column.setPrimaryKey(true);
                    break;
                }
            }
            if (!found)
                throw new SQLException("Cannot find the column in primary key constraint");
        }
    }
}
