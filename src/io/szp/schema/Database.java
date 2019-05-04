package io.szp.schema;

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
    public Database(String root) {
        this.tables = new HashMap<>();
        this.root = root;
    }

    /**
     * 返回根目录。
     *
     * @return 根目录
     */
    public String getRoot() {
        return root;
    }
}
