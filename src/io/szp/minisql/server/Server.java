package io.szp.minisql.server;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;

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
    private Global global;
    /**
     * 构造函数初始化连接的信息（但不监听）。
     *
     * @param port 端口
     * @param host 主机
     * @param root 数据库的根目录
     * @throws SQLException 无法读取数据库列表
     */
    public Server(int port, String host, String root) throws SQLException {
        this.port = port;
        this.host = host;
        this.global = new Global(root);
    }
    /**
     * 监听端口，对于每个连接，启动一个新的线程，调用handler。
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName(host))) {
            if (ServerConfig.verbose)
                System.out.println("Listening at " + serverSocket);
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (ServerConfig.verbose)
                    System.out.println("Accept client from " + clientSocket);
                new Thread(() -> {
                    try (Socket copy = clientSocket) { // java 9 feature
                        ClientHandler handler = new ClientHandler(copy, global);
                        handler.run();
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
