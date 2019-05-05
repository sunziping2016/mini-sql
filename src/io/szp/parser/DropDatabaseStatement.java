package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

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
