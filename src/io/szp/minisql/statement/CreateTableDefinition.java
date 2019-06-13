package io.szp.minisql.statement;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;

import java.util.ArrayList;

/**
 * 表创建中的列定义及限制部分。
 */
public interface CreateTableDefinition {
    void apply(ArrayList<Column> columns) throws SQLException;
}
