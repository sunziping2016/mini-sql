package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.schema.*;

public class ShowDatabasesStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Table result = new Table(new Column[] {
                new Column("DATABASES", Type.STRING)
        }, "RESULT");
        for (String name : global.getDatabasesList())
            result.addRow(new Object[] { name });
        return result;
    }
}
