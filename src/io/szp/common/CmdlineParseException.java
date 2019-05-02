package io.szp.common;

/**
 * 代表命令行解析出错的类。
 */
public class CmdlineParseException extends Exception {
    /**
     * 命令行解析出错异常类的构造函数。
     *
     * @param message 出错信息
     */
    public CmdlineParseException(String message) {
        super(message);
    }
}
