package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.*;

public class ShowDatabasesStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Table result = new Table(new Column[] {
                new Column("databases", Type.STRING)
        });
        for (var name : global.getDatabasesList())
            result.addRow(new Object[] { name });
        return result;
    }
}
