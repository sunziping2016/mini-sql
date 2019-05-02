package io.szp.server;

/**
 * 这个类存放一些服务端用的参数（包括常量和运行时可改变共享的参数）。
 */
public class ServerConfig {
    /**
     * 服务端默认监听的端口。
     */
    public static final int DEFAULT_PORT = 24620;
    /**
     * 服务端默认监听的地址。
     */
    public static final String DEFAULT_HOST = "127.0.0.1";
    /**
     * 是否打印详细信息。
     */
    public static boolean verbose = false;
}
