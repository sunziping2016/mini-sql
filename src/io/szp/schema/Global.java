package io.szp.schema;

import io.szp.exception.SQLException;
import io.szp.exception.SyntaxException;
import io.szp.parser.SQLLexer;
import io.szp.parser.SQLParser;
import io.szp.parser.Visitor;
import io.szp.server.SQLThrowErrorListener;
import io.szp.statement.Statement;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 存储全局数据
 */
public class Global {
    private HashMap<String, Database> databases;
    private String root;
    private ArrayList<Session> sessions;
    /**
     * 从数据库根目录加载数据库列表。
     *
     * @param root 根目录
     * @throws SQLException 无法读取数据库列表
     */
    public Global(String root) throws SQLException {
        this.databases = new HashMap<>();
        this.root = root;
        this.sessions = new ArrayList<>();

        loadDatabases();
    }

    /**
     * 将会话添加到全局会话列表中。
     *
     * @param session 要添加的会话
     */
    public synchronized void addSession(Session session) {
        sessions.add(session);
    }

    /**
     * 将会话从全局会话列表中删除。
     *
     * @param session 要删除的会话。
     */
    public synchronized void removeSession(Session session) {
        sessions.remove(session);
    }

    /**
     * 创建数据库，会先创建目录。
     *
     * @param name 数据库名字
     * @throws SQLException 创建失败
     */
    public synchronized void addDatabase(String name) throws SQLException {
        if (databases.containsKey(name))
            throw new SQLException("Database already exists");
        String new_root = Paths.get(root, name).toString();
        if (!new File(new_root).mkdir())
            throw new SQLException("Cannot create database");
        databases.put(name, new Database(new_root));
    }

    /**
     * 删除数据库，会删除目录，如果有Session使用该数据库，会退出。
     *
     * @param name 数据库名字
     * @throws SQLException 删除失败
     */
    public synchronized void removeDatabase(String name) throws SQLException {
        if (!databases.containsKey(name))
            throw new SQLException("Database does not exist");
        Database database = databases.get(name);
        if (!deleteRecursive(new File(database.getRoot())))
            throw new SQLException("Failed to delete database");
        for (Session session : sessions)
            if (session.getCurrentDatabase() == database)
                session.setCurrentDatabase(null);
        databases.remove(name);
    }

    public synchronized void removeAllDatabases() throws SQLException {
        for (String name : databases.keySet())
            removeDatabase(name);
    }

    /**
     * 获取数据库。
     *
     * @param name 数据库名
     * @throws SQLException 数据库不存在
     */
    public synchronized Database getDatabase(String name) throws SQLException{
        Database database = databases.get(name);
        if (database == null)
            throw new SQLException("Database does not exist");
        return database;
    }

    public synchronized String[] getDatabasesList() {
        return databases.keySet().toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    public Table execute(String command, Session session) throws SQLException, SyntaxException {
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
        for (Statement statement: statements)
            result = statement.execute(this, session);
        return result;
    }

    private void loadDatabases() throws SQLException {
        databases.clear();
        String[] list = new File(root).list();
        if (list != null) {
            for (String item : list)
                databases.put(item, new Database(Paths.get(root, item).toString()));
        } else
            throw new SQLException("List databases failed");
    }

    private static boolean deleteRecursive(File path) {
        boolean ret = true;
        if (path.isDirectory()){
            File[] files = path.listFiles();
            if (files != null)
                for (File f : files)
                    ret = ret && deleteRecursive(f);
        }
        return ret && path.delete();
    }

}
