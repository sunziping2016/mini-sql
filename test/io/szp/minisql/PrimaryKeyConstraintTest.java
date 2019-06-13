package io.szp.minisql;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrimaryKeyConstraintTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();

        global.removeAllDatabases();
    }

    // 删除后删除的主键可用
    @Test
    public void primaryKeyConstraintRemoveRow() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j long, primary key (i, j))", session);
        global.execute("insert into test values (1, 2), (1, 3)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (1, 3)", session);
        });
        assertEquals("Violate primary key constraint", e.getMessage());
        global.execute("delete from test where j = 3", session);
        global.execute("insert into test values (1, 3)", session);
        e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (1, 3)", session);
        });
        assertEquals("Violate primary key constraint", e.getMessage());
    }

    // 更改后旧删主键可用，新加主键不可用
    @Test
    public void PrimaryKeyConstraintUpdateRow() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j long, primary key (i, j))", session);
        global.execute("insert into test values (1, 2), (1, 3)", session);
        global.execute("update test set j = 4 where j = 3", session);
        global.execute("insert into test values (1, 3)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (1, 3)", session);
        });
        assertEquals("Violate primary key constraint", e.getMessage());
        e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (1, 4)", session);
        });
        assertEquals("Violate primary key constraint", e.getMessage());
    }
}
