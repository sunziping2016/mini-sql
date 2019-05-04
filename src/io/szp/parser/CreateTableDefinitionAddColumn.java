package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.Column;

import java.util.ArrayList;

public class CreateTableDefinitionAddColumn implements CreateTableDefinition {
    private Column column;

    public CreateTableDefinitionAddColumn(Column column) {
        this.column = column;
    }

    @Override
    public void apply(ArrayList<Column> columns) throws SQLException {
        for (var c : columns)
            if (c.getName().equals(column.getName()))
                throw new SQLException("Duplicated column name");
        columns.add(column);
    }
}
