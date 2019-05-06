package io.szp.server;

import io.szp.exception.SQLException;
import io.szp.exception.SyntaxException;
import io.szp.parser.SQLLexer;
import io.szp.parser.SQLParser;
import io.szp.statement.Statement;
import io.szp.parser.Visitor;
import io.szp.schema.Global;
import io.szp.schema.Session;
import io.szp.schema.Table;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        Session session = new Session();
        global.addSession(session);
        try (var out = new ObjectOutputStream(socket.getOutputStream());
             var in = new ObjectInputStream(socket.getInputStream());
        ) {
            while (true) {
                try {
                    String command = (String) in.readObject();
                    command = command.toUpperCase();
                    // 解析命令
                    SQLThrowErrorListener listener = new SQLThrowErrorListener();
                    SQLLexer lexer = new SQLLexer(CharStreams.fromString(command));
                    lexer.removeErrorListeners();
                    lexer.addErrorListener(listener);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    SQLParser parser = new SQLParser(tokens);
                    parser.removeErrorListeners();
                    parser.addErrorListener(listener);
                    ParseTree tree = parser.root();
                    // 遍历语法树
                    Visitor visitor = new Visitor();
                    ArrayList<Statement> statements = (ArrayList<Statement>) visitor.visit(tree);
                    // 执行语句
                    Table result = null;
                    for (var statement: statements)
                        result = statement.execute(global, session);
                    out.writeObject(result);
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
