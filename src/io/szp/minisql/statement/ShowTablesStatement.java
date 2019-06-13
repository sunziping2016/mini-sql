package io.szp.minisql.statement;


import io.szp.minisql.Database;
import io.szp.minisql.Session;
import io.szp.minisql.Type;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

public class ShowTablesStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        Table result = new Table("RESULT",
                new Column[] { new Column("TABLES", Type.STRING)});
        for (String name : database.getTablesList())
            result.addRow(new Object[] { name });
        return result;
    }
}
