package io.szp.exception;

/**
 * 代表数据库发生了一个逻辑上不该有的内部错误。
 */
public class InternalException extends Exception {
    /**
     * 内部错误的构造函数。
     *
     * @param message 出错信息
     */
    public InternalException(String message) {
        super(message);
    }
}
