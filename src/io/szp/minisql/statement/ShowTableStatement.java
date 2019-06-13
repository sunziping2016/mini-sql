package io.szp.minisql.statement;

import io.szp.minisql.Database;
import io.szp.minisql.Session;
import io.szp.minisql.Type;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

public class ShowTableStatement implements Statement {
    private String name;

    public ShowTableStatement(String name) {
        this.name = name;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        Table table = database.getTable(name);
        Column[] columns = table.getColumns();
        Table result = new Table("RESULT",
                new Column[] {
                        new Column("NAME", Type.STRING),
                        new Column("TYPE", Type.STRING),
                        new Column("NOT NULL", Type.INT),
                        new Column("PRIMARY KEY", Type.INT),
                }
        );
        for (Column column : columns) {
            Object[] row = new Object[4];
            row[0] = column.getName();
            switch (column.getType()) {
                case INT:
                    row[1] = "INT";
                    break;
                case LONG:
                    row[1] = "LONG";
                    break;
                case FLOAT:
                    row[1] = "FLOAT";
                    break;
                case DOUBLE:
                    row[1] = "DOUBLE";
                    break;
                case STRING:
                    row[1] = "STRING";
                    break;
            }
            row[2] = column.isNotNull() ? 1 : 0;
            row[3] = column.isPrimaryKey() ? 1 : 0;
            result.addRow(row);
        }
        return result;
    }
}
