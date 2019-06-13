package io.szp.minisql.statement;

import io.szp.minisql.Session;
import io.szp.minisql.Type;
import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;

public class ShowDatabasesStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Table result = new Table("RESULT", new Column[] {
                new Column("DATABASES", Type.STRING)});
        for (String name : global.getDatabasesList())
            result.addRow(new Object[] { name });
        return result;
    }
}
