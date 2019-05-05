package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

/**
 * 创建数据库的SQL语句。
 */
public class CreateDatabaseStatement extends Statement {
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