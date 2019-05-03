package io.szp.schema;

import io.szp.exception.TableCorruptedException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Table {
    // 保存的数据
    private Column[] columns;
    private ArrayList<Object[]> table;
    // 计算而得的数据
    private HashSet<Object[]> primary_key_index;

    /**
     * 从流中读取数据。
     *
     * @param in 输入流
     * @throws IOException            IO错误
     * @throws ClassNotFoundException 找不到对应的类
     */
    public void load(ObjectInputStream in) throws TableCorruptedException {
        try {
            columns = (Column[]) in.readObject();
            //noinspection unchecked
            table = (ArrayList<Object[]>) in.readObject();

            // TODO: check integration
            buildPrimaryKeyIndex();
        } catch (IOException e) {
            throw new TableCorruptedException("Unexpected IO exception");
        } catch (ClassNotFoundException e) {
            throw new TableCorruptedException("Serialized class not found");
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
        out.writeObject(table);
    }

    /**
     * 建立主键约束的哈希表，用以检查是否冲突。
     */
    private void buildPrimaryKeyIndex() throws TableCorruptedException {
        ArrayList<Integer> keys = new ArrayList<>();
        for (int i = 0; i < columns.length; ++i)
            if (columns[i].isPrimaryKey())
                keys.add(i);
        primary_key_index = new HashSet<>();
        for (Object[] row: table) {
            Object[] item = new Object[keys.size()];
            for (int i = 0; i < keys.size(); ++i)
                item[i] = row[keys.get(i)];
            if (primary_key_index.contains(item))
                throw new TableCorruptedException("Duplicated primary key");
            primary_key_index.add(item);
        }
    }
}
