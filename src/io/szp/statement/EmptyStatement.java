package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

public class EmptyStatement implements Statement {
    @Override
    public Table execute(Global global, Session session) throws SQLException {
        return null;
    }
}
