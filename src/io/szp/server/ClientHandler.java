package io.szp.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 处理SQL的客户端连接。
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    /**
     * 构造函数。
     *
     * @param socket 客户端socket
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    /**
     * 处理SQL连接。
     */
    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            while (true) {
                out.write(in.read());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
