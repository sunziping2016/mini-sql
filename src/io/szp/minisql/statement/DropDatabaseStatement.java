package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import io.szp.minisql.Session;
import io.szp.minisql.schema.Table;

/**
 * 删除数据库的SQL语句
 */
public class DropDatabaseStatement implements Statement {
    private String name;

    /**
     * 构造函数。
     *
     * @param name 数据库名
     */
    public DropDatabaseStatement(String name) {
        this.name = name;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        global.removeDatabase(name);
        return null;
    }
}
