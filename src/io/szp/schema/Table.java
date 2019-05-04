package io.szp.schema;

import io.szp.exception.SQLException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Table implements Serializable {
    // 保存的数据
    private Column[] columns;
    private ArrayList<Object[]> data;
    // 计算而得的数据
    private HashSet<Object[]> primary_key_index;

    /**
     * 创建空表。
     */
    public Table() {
        columns = new Column[0];
        data = new ArrayList<>();
        primary_key_index = new HashSet<>();
    }

    /**
     * 从流中读取数据。
     *
     * @param in 输入流
     * @throws SQLException 表的完整性出错
     */
    public void load(ObjectInputStream in) throws SQLException {
        try {
            columns = (Column[]) in.readObject();
            //noinspection unchecked
            data = (ArrayList<Object[]>) in.readObject();
            // TODO: check integration
            buildPrimaryKeyIndex();
        } catch (IOException e) {
            throw new SQLException("Unexpected IO exception");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Serialized class not found");
        }
    }

    /**
     * 向流写入数据。
     *
     * @param out 输出流
     * @throws IOException IO错误
     */
    public void save(ObjectOutputStream out) throws IOException {
        out.writeObject(columns);
        out.writeObject(data);
    }

    /**
     * 打印表。
     *
     * @param out 输出
     */
    public void print(OutputStream out) {
        // TODO
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
}
