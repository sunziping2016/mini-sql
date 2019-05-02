package io.szp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端类。
 */
public class Server implements Runnable {
    private int port;
    private String host;
    private ClientHandlerInterface handler;
    /**
     * 构造函数初始化连接的信息（但不监听）。
     *
     * @param port 端口
     * @param host 主机
     * @param handler 处理客户端连接
     */
    public Server(int port, String host, ClientHandlerInterface handler) {
        this.port = port;
        this.host = host;
        this.handler = handler;
    }
    /**
     * 监听端口，对于每个连接，启动一个新的线程，调用handler
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName(host))) {
            System.out.println("Listening at " + serverSocket);
            if (ServerConfig.verbose)
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (ServerConfig.verbose)
                    System.out.println("Accept client from " + clientSocket);
                new Thread(() -> {
                    try (clientSocket) { // java 9 feature
                        handler.handle(clientSocket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Close client from " + clientSocket);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
