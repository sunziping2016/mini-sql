package io.szp.schema;

import io.szp.exception.SQLException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Table implements Serializable {
    // 保存的数据
    private Column[] columns;
    private ArrayList<Object[]> data;
    // 不保存但不是计算而得的数据
    private String root;
    private String name;
    // 计算而得的数据
    ArrayList<Integer> primary_keys;
    private HashSet<ArrayList<Object>> primary_key_index;
    private HashMap<String, Integer> column_index;
    /**
     * 创建空表。
     */
    public Table(String name) throws SQLException {
        this(new Column[0], name);
    }

    public Table(Column[] columns, String name) throws SQLException {
        this.columns = columns;
        data = new ArrayList<>();
        root = "";
        name = name;

        buildPrimaryKeyIndex();
        buildColumnIndex();
    }

    public synchronized String getRoot() {
        return root;
    }

    public synchronized void setRoot(String root) {
        this.root = root;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String root) {
        this.root = name;
    }

    /**
     * 从流中读取数据。
     *
     * @throws SQLException 表的完整性出错
     */
    public synchronized void load() throws SQLException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(root))) {
            columns = (Column[]) in.readObject();
            //noinspection unchecked
            data = (ArrayList<Object[]>) in.readObject();
            // TODO: check integration
            buildPrimaryKeyIndex();
            buildColumnIndex();
        } catch (Exception e) {
            throw new SQLException("Cannot load table");
        }
    }

    /**
     * 向流写入数据。
     *
     * @throws SQLException IO错误
     */
    public synchronized void save() throws SQLException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(root))) {
            out.writeObject(columns);
            out.writeObject(data);
        } catch (Exception e) {
            throw new SQLException("Cannot save table");
        }
    }

    /**
     * 打印表。
     *
     * @param out 输出
     */
    public synchronized void print(PrintStream out) {
        if (columns.length > 0)
            out.print(columns[0].getName());
        for (int i = 1; i < columns.length; ++i) {
            out.print(", ");
            out.print(columns[i].getName());
        }
        out.println();
        for (Object[] row : data) {
            if (row.length > 0)
                out.print(row[0]);
            for (int i = 1; i < row.length; ++i) {
                out.print(", ");
                out.print(row[i]);
            }
            out.println();
        }
    }

    public synchronized void addRow(Object[] row) throws SQLException {
        if (row.length != columns.length)
            throw new SQLException("Wrong size of row");
        for (int i = 0; i < columns.length; ++i)
            if (columns[i].isNotNull() && row[i] == null)
                throw new SQLException("Violate not null constraint");
            if (!primary_keys.isEmpty()) {
                ArrayList<Object> item = new ArrayList<>();
                for (Integer key : primary_keys)
                    item.add(row[key]);
                if (primary_key_index.contains(item))
                    throw new SQLException("Violate primary key constraint");
                primary_key_index.add(item);
            }
        data.add(row);
    }

    public synchronized Column[] getColumns() {
        return columns;
    }

    public synchronized HashMap<String, Integer> getColumnIndex() {
        return column_index;
    }

    /**
     * 建立主键约束的哈希表，用以检查是否冲突。
     */
    private void buildPrimaryKeyIndex() throws SQLException {
        primary_keys = new ArrayList<>();
        for (int i = 0; i < columns.length; ++i)
            if (columns[i].isPrimaryKey())
                primary_keys.add(i);
        primary_key_index = new HashSet<>();
        if (primary_keys.isEmpty())
            return;
        for (Object[] row: data) {
            ArrayList<Object> item = new ArrayList<>();
            for (Integer key : primary_keys)
                item.add(row[key]);
            if (primary_key_index.contains(item))
                throw new SQLException("Duplicated primary key");
            primary_key_index.add(item);
        }
    }

    private void buildColumnIndex() throws SQLException {
        column_index = new HashMap<>();
        for (int i = 0; i < columns.length; ++i) {
            String name = columns[i].getName();
            if (column_index.containsKey(name))
                throw new SQLException("Duplicated column name");
            column_index.put(name, i);
        }
    }
}
