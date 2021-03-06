package io.szp.minisql.statement;

import io.szp.minisql.Database;
import io.szp.minisql.Session;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

import java.util.ArrayList;

public class CreateTableStatement implements Statement {
    private String name;
    private ArrayList<CreateTableDefinition> definitions;

    public CreateTableStatement(String name, ArrayList<CreateTableDefinition> definitions) {
        this.name = name;
        this.definitions = definitions;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        ArrayList<Column> columns = new ArrayList<>();
        for (CreateTableDefinition definition : definitions)
            definition.apply(columns);
        database.addTable(name, columns.toArray(new Column[0]));
        return null;
    }
}
