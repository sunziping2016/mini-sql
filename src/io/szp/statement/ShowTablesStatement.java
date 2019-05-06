package io.szp.statement;


import io.szp.exception.SQLException;
import io.szp.schema.*;

public class ShowTablesStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        Table result = new Table(new Column[] {
                new Column("tables", Type.STRING)
        },"result");
        for (var name : database.getTablesList())
            result.addRow(new Object[] { name });
        return result;
    }
}
