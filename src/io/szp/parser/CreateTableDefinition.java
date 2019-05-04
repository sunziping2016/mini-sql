package io.szp.parser;

import io.szp.exception.SQLException;
import io.szp.schema.Column;

import java.util.ArrayList;

/**
 * 表创建中的列定义及限制部分。
 */
interface CreateTableDefinition {
    void apply(ArrayList<Column> columns) throws SQLException;
}
