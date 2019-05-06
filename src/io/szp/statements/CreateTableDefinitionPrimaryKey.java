package io.szp.statements;

import io.szp.exception.SQLException;
import io.szp.schema.Column;

import java.util.ArrayList;

public class CreateTableDefinitionPrimaryKey implements CreateTableDefinition {
    private ArrayList<String> names;

    public CreateTableDefinitionPrimaryKey(ArrayList<String> names) {
        this.names = names;
    }

    @Override
    public void apply(ArrayList<Column> columns) throws SQLException {
        for (var name : names) {
            boolean found = false;
            for (var column : columns) {
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
