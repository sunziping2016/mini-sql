package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.*;

import java.util.ArrayList;

public class CreateTableStatement extends Statement {
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
        for (var definition : definitions)
            definition.apply(columns);
        database.addTable(name, columns.toArray(new Column[0]));
        return null;
    }
}
