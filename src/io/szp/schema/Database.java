package io.szp.schema;

import io.szp.exception.SQLException;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * 代表一个数据库的类。
 */
public class Database {
    private HashMap<String, Table> tables;
    private String root;

    /**
     * 构造函数。
     *
     * @param root 该数据库的根目录
     */
    public Database(String root) throws SQLException {
        this.tables = new HashMap<>();
        this.root = root;

        loadTables();
    }

    /**
     * 返回根目录。
     *
     * @return 根目录
     */
    public synchronized String getRoot() {
        return root;
    }

    public synchronized void addTable(String name, Column[] columns) throws SQLException {
        if (tables.containsKey(name))
            throw new SQLException("Table already exists");
        String new_root = Paths.get(root, name).toString();
        Table table = new Table(columns, name);
        table.setRoot(new_root);
        table.save();
        tables.put(name, table);
    }

    public synchronized Table getTable(String name) throws SQLException {
        if (!tables.containsKey(name))
            throw new SQLException("Table does not exist");
        return tables.get(name);
    }

    public synchronized void removeTable(String name) throws SQLException {
        if (!tables.containsKey(name))
            throw new SQLException("Table does not exist");
        Table table = tables.get(name);
        if (!new File(table.getRoot()).delete())
            throw new SQLException("Failed to delete table");
        tables.remove(name);
    }

    public synchronized String[] getTablesList() {
        return tables.keySet().toArray(new String[0]);
    }

    private void loadTables() throws SQLException {
        tables.clear();
        String[] list = new File(root).list();
        if (list != null) {
            for (String item : list) {
                Table table = new Table(item);
                table.setRoot(Paths.get(root, item).toString());
                table.load();
                tables.put(item, table);
            }
        } else
            throw new SQLException("List databases failed");
    }
}
