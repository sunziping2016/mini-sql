package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.Database;
import io.szp.minisql.schema.Global;
import io.szp.minisql.Session;
import io.szp.minisql.schema.Table;

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
