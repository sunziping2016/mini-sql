package io.szp.server;

import io.szp.parser.SQLLexer;
import io.szp.parser.SQLParser;
import io.szp.schema.Global;
import io.szp.schema.Session;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        ) {
            while (true) {
                // 抽取命令
                StringBuilder command = new StringBuilder();
                String temp;
                while ((temp = in.readLine()) != null && !temp.isEmpty())
                    command.append(temp).append('\n');
                if (temp == null)
                    break;
                out.write(command.toString());
                // 解析命令
                SQLLexer lexer = new SQLLexer(CharStreams.fromString(command.toString()));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                SQLParser parser = new SQLParser(tokens);
                ParseTree tree = parser.root();
                //
                out.write(tree.toStringTree(parser) + "\n");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            global.removeSession(session);
        }
    }
}
