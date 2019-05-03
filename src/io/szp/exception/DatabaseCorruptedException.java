package io.szp.exception;

/**
 * 代表数据库的完整性出错的异常。
 */
public class DatabaseCorruptedException extends SQLException {
    /**
     * 数据库的完整性出错异常类的构造函数。
     *
     * @param message 出错信息
     */
    public DatabaseCorruptedException(String message) {
        super(message);
    }
}
