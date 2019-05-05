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
    // 计算而得的数据
    private HashSet<Object[]> primary_key_index;
    private HashMap<String, Integer> column_index;
    /**
     * 创建空表。
     */
    public Table() throws SQLException {
        this(new Column[0]);
    }

    public Table(Column[] columns) throws SQLException {
        this.columns = columns;
        data = new ArrayList<>();
        root = "";

        buildPrimaryKeyIndex();
        buildColumnIndex();
    }

    public synchronized String getRoot() {
        return root;
    }

    public synchronized void setRoot(String root) {
        this.root = root;
    }

    /**
     * 从流中读取数据。
     *
     * @throws SQLException 表的完整性出错
     */
    public synchronized void load() throws SQLException {
        try (var in = new ObjectInputStream(new FileInputStream(root))) {
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
        try (var out = new ObjectOutputStream(new FileOutputStream(root))) {
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
        for (var row : data) {
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
        data.add(row);
    }

    /**
     * 建立主键约束的哈希表，用以检查是否冲突。
     */
    private void buildPrimaryKeyIndex() throws SQLException {
        ArrayList<Integer> keys = new ArrayList<>();
        for (int i = 0; i < columns.length; ++i)
            if (columns[i].isPrimaryKey())
                keys.add(i);
        primary_key_index = new HashSet<>();
        if (keys.isEmpty())
            return;
        for (Object[] row: data) {
            Object[] item = new Object[keys.size()];
            for (int i = 0; i < keys.size(); ++i)
                item[i] = row[keys.get(i)];
            if (primary_key_index.contains(item))
                throw new SQLException("Duplicated primary key");
            primary_key_index.add(item);
        }
    }

    private void buildColumnIndex() {
        column_index = new HashMap<>();
        for (int i = 0; i < columns.length; ++i)
            column_index.put(columns[i].getName(), i);
    }
}
