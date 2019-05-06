package io.szp.statement;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;

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
