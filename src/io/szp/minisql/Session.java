package io.szp.minisql;

/**
 * 每个连接拥有的回话。
 */
public class Session {
    private Database currentDatabase;

    /**
     * 构造函数
     */
    public Session() {
        currentDatabase = null;
    }

    /**
     * 返回当前的数据库。
     *
     * @return 当前数据库
     */
    public Database getCurrentDatabase() {
        return currentDatabase;
    }

    /**
     * 设置当前的数据库。由于global对象可能调用setCurrentDatabase，所以需要同步。
     *
     * @param currentDatabase 要设为当前的数据库
     */
    public synchronized void setCurrentDatabase(Database currentDatabase) {
        this.currentDatabase = currentDatabase;
    }
}
