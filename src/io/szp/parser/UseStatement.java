package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

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
