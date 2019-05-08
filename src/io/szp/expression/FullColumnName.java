package io.szp.expression;

public class FullColumnName {
    private String table_name;
    private String column_name;

    public FullColumnName(String table_name, String column_name) {
        this.table_name = table_name;
        this.column_name = column_name;
    }

    public String getTableName() {
        return table_name;
    }

    public String getColumnName() {
        return column_name;
    }
}
