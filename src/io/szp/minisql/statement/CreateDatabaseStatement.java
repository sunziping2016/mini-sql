package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import io.szp.minisql.Session;
import io.szp.minisql.schema.Table;

/**
 * 创建数据库的SQL语句。
 */
public class CreateDatabaseStatement implements Statement {
    private String name;

    /**
     * 构造函数。
     *
     * @param name 数据库名
     */
    public CreateDatabaseStatement(String name) {
        this.name = name;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        global.addDatabase(name);
        return null;
    }
}
