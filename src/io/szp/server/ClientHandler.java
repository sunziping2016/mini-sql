package io.szp.server;

import io.szp.exception.SQLException;
import io.szp.exception.SyntaxException;
import io.szp.schema.Global;
import io.szp.schema.Session;

import java.io.*;
import java.net.Socket;

/**
 * 处理SQL的客户端连接。
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private Global global;
    /**
     * 构造函数。
     *
     * @param socket 客户端socket
     * @param global 全局共享对象
     */
    public ClientHandler(Socket socket, Global global) {
        this.socket = socket;
        this.global = global;
    }
    /**
     * 处理SQL连接。
     */
    @Override
    public void run() {
        Session session = new Session();
        global.addSession(session);
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            while (true) {
                try {
                    String command = (String) in.readObject();
                    out.writeObject(global.execute(command, session));
                    out.flush();
                } catch (IOException e) {
                    break;
                } catch (SQLException | SyntaxException e) {
                    out.writeObject(e.getMessage());
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    out.writeObject("Internal error");
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            global.removeSession(session);
        }
    }
}
