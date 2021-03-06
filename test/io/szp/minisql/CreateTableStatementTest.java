package io.szp.minisql;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateTableStatementTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();
        global.removeAllDatabases();
    }

    // 插入表之前没有选择数据库
    @Test
    public void createTableDatabaseNotSelected() throws SQLException {
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("create table test (i int)", session);
        });
        assertEquals("No database selected", e.getMessage());
    }

    // 创建两张重名的表
    @Test
    public void createTableDuplicatedTableName() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("create table test (i int)", session);
        });
        assertEquals("Table already exists", e.getMessage());
    }

    // 插入表的列名重复
    @Test
    public void createTableDuplicatedColumnName() throws SQLException {
        global.execute("create database test; use test", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("create table test (i int, i int)", session);
        });
        assertEquals("Duplicated column name", e.getMessage());
    }

    // 插入表的Not Null限制
    @Test
    public void createTableNotNullConstraint() throws SQLException {
        // 实现请参考下面的Primary Key，注意not null只有下面两种声明方式的第一种
        global.execute("create database test; use test", session);
        global.execute("create table test (i int not null, j int)", session);
        Table copy = new Global(Config.root).getDatabase("TEST").getTable("TEST");
        assertEquals(new Table("TEST",
                new Column[] {
                        new Column("I", Type.INT, true, false),
                        new Column("J", Type.INT, false, false)
                }
        ), copy);
    }

    // 插入表的Primary Key限制
    @Test
    public void createTablePrimaryKeyConstraint() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int primary key, j int)", session);
        Table copy = new Global(Config.root).getDatabase("TEST").getTable("TEST");
        assertEquals(new Table("TEST",
                new Column[] {
                        new Column("I", Type.INT, true, true),
                        new Column("J", Type.INT, false, false)
                }
        ), copy);
        global.execute("create table test2 (i int primary key, j int, k int, primary key (i, j))", session);
        copy = new Global(Config.root).getDatabase("TEST").getTable("TEST2");
        assertEquals(new Table("TEST2",
                new Column[] {
                        new Column("I", Type.INT, true, true),
                        new Column("J", Type.INT, true, true),
                        new Column("K", Type.INT, false, false)
                }
        ), copy);
    }
}
