package io.szp.schema;

import java.util.HashMap;

/**
 * 代表一个数据库的类。
 */
public class Database {
    private HashMap<String, Table> tables;
    private String root;

    public Database(String root) {
        this.tables = new HashMap<>();
        this.root = root;
    }
}
