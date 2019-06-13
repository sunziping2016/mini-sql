package io.szp.minisql.expression;

import io.szp.minisql.exception.SQLException;

public interface Variables {
    class Position {
        public int table;
        public int column;

        public Position(int table, int column) {
            this.table = table;
            this.column = column;
        }
    }

    Position getPosition(FullColumnName full_column_name) throws SQLException;

    ExpressionType getType(Position position);
    Object getValue(Position position);
}
