package io.szp.minisql.expression;

import java.util.Arrays;
import java.util.Objects;

public class FullColumnName {
    private String table_name;
    private String column_name;

    public FullColumnName(String table_name, String column_name) {
        this.table_name = table_name;
        this.column_name = column_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FullColumnName name = (FullColumnName) obj;
        return Objects.equals(table_name, name.table_name) &&
                Objects.equals(column_name, name.column_name);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                table_name,
                column_name
        });
    }
}
