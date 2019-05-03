package io.szp.exception;

/**
 * 代表表的完整性出错的异常。
 */
public class TableCorruptedException extends SQLException {
    /**
     * 表的完整性出错异常类的构造函数。
     *
     * @param message 出错信息
     */
    public TableCorruptedException(String message) {
        super(message);
    }
}
