package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import io.szp.minisql.Session;
import io.szp.minisql.schema.Table;

/**
 * 使用数据库。
 */
public class UseStatement implements Statement {
    private String name;

    /**
     * 构造函数。
     *
     * @param name 数据库名
     */
    public UseStatement(String name) {
        this.name = name;
    }

    @Override
    public Table execute(Global global, Session session) throws SQLException {
        session.setCurrentDatabase(global.getDatabase(name));
        return null;
    }
}
