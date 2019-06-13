package io.szp.minisql.exception;

/**
 * 代表一个能从服务端传递给客户端的错误。
 */
public class SQLException extends Exception {
    /**
     * 构造函数，创建一个错误。
     *
     * @param message 错误信息
     */
    public SQLException(String message) {
        super(message);
    }
}
