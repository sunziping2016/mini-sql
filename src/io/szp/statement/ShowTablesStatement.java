package io.szp.statement;


import io.szp.exception.SQLException;
import io.szp.schema.*;

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
