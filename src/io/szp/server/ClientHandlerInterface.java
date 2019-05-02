package io.szp.server;

import java.net.Socket;

/**
 * 具体处理客户端连接的接口
 */
public interface ClientHandlerInterface {
    /**
     * 处理客户端连接的函数。
     *
     * @param socket 客户端socket
     */
    void handle(Socket socket);
}
