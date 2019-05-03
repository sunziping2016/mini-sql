package io.szp.exception;

/**
 * 代表数据库发生了一个错误。
 */
public class SQLException extends Exception {
    /**
     * 构造函数。
     *
     * @param message 出错信息
     */
    public SQLException(String message) {
        super(message);
    }
}
