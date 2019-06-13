package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import io.szp.minisql.Session;
import io.szp.minisql.schema.Table;

/**
 * 这个类代表一个语句。
 */
public interface Statement {
    /**
     * 执行并返回结果
     * @return 结果
     */
    public Table execute(Global global, Session session) throws SQLException;
}
