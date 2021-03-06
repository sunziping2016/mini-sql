package io.szp.minisql.schema;

import io.szp.minisql.exception.SQLException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Table implements Serializable {
    private String name;
    // 保存的数据
    private Column[] columns;
    private ArrayList<Object[]> data;
    // 不保存但不是计算而得的数据
    private String root;
    // 计算而得的数据
    private ArrayList<Integer> primary_keys;
    private HashSet<ArrayList<Object>> primary_key_index;
    private HashMap<String, Integer> column_index;
    /**
     * 创建空表。
     */
    public Table(String name) throws SQLException {
        this(name, new Column[0], new Object[0][]);
    }

    public Table(String name, Column[] columns) throws SQLException {
        this(name, columns, new Object[0][]);
    }

    public Table(String name, Column[] columns, Object[][] data) throws SQLException {
        this.name = name;
        this.columns = columns;
        this.data = new ArrayList<>(Arrays.asList(data));
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

    public synchronized void removeRow(int row) throws SQLException {
        if (!primary_keys.isEmpty()) {
            Object[] row_data = data.get(row);
            ArrayList<Object> item = new ArrayList<>();
            for (Integer key : primary_keys)
                item.add(row_data[key]);
            primary_key_index.remove(item);
        }
        data.remove(row);
    }

    public synchronized void updateRow(int row, Object[] new_row_data) throws SQLException {
        for (int i = 0; i < columns.length; ++i)
            if (columns[i].isNotNull() && new_row_data[i] == null)
                throw new SQLException("Violate not null constraint");
        Object[] old_row_data = data.get(row);
        if (!primary_keys.isEmpty()) {
            ArrayList<Object> item = new ArrayList<>();
            for (Integer key : primary_keys)
                item.add(old_row_data[key]);
            primary_key_index.remove(item);
        }
        System.arraycopy(new_row_data, 0, old_row_data, 0, columns.length);
        if (!primary_keys.isEmpty()) {
            ArrayList<Object> item = new ArrayList<>();
            for (Integer key : primary_keys)
                item.add(new_row_data[key]);
            primary_key_index.add(item);
        }
    }

    public synchronized int getColumnSize() {
        return columns.length;
    }

    public synchronized int getRowSize() {
        return data.size();
    }

    public synchronized Column[] getColumns() {
        return columns;
    }

    public synchronized Column getColumn(int index) {
        return columns[index];
    }

    public synchronized ArrayList<Object []> getData() {
        return data;
    }

    public synchronized Object[] getData(int row) {
        return data.get(row);
    }

    public synchronized Object getData(int row, int column) {
        return data.get(row)[column];
    }

    public synchronized HashMap<String, Integer> getColumnIndex() {
        return column_index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Table other = (Table) obj;
        return name.equals(other.name) &&
                Arrays.equals(columns, other.columns) &&
                Arrays.deepEquals(data.toArray(), other.data.toArray());
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
            column_index.put(name, i);
        }
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(out, true, StandardCharsets.UTF_8.name())) {
            print(stream);
        } catch (UnsupportedEncodingException e) {
            // do nothing
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
