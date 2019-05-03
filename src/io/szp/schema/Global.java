package io.szp.schema;

import io.szp.exception.DatabaseCorruptedException;
import io.szp.server.ServerConfig;

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
     * @throws DatabaseCorruptedException 无法读取数据库列表
     */
    public Global(String root) throws DatabaseCorruptedException {
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

    private void loadDatabases() throws DatabaseCorruptedException {
        databases.clear();
        String[] list = new File(root).list();
        if (list != null) {
            for (var item : list) {
                databases.put(item, new Database(Paths.get(root, item).toString()));
                if (ServerConfig.verbose)
                    System.out.println("Loaded database \"" + item + "\"");
            }
        } else
            throw new DatabaseCorruptedException("List databases failed");
    }
}
