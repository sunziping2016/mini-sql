package io.szp.exception;

/**
 * 代表表的完整性出错的异常
 */
public class TableCorruptedException extends InternalException {
    /**
     * 命令行解析出错异常类的构造函数。
     *
     * @param message 出错信息
     */
    public TableCorruptedException(String message) {
        super(message);
    }
}
