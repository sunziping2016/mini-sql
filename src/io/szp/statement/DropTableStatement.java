package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.schema.Database;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

/**
 * 删除表的SQL语句
 */
public class DropTableStatement implements Statement {
    private String name;

    /**
     * 构造函数。
     *
     * @param name 表名
     */
    public DropTableStatement(String name) {
        this.name = name;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        Database database = session.getCurrentDatabase();
        if (database == null)
            throw new SQLException("No database selected");
        database.removeTable(name);
        return null;
    }
}
