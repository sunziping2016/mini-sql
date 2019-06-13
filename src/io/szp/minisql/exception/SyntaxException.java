package io.szp.minisql.exception;

/**
 * 输入语法有错。
 */
public class SyntaxException extends RuntimeException {
    /**
     * 构造函数。
     *
     * @param message 出错信息
     */
    public SyntaxException(String message) {
        super(message);
    }

}
