package io.szp;

import io.szp.exception.SQLException;
import io.szp.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InsertStatementTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();

        global.removeAllDatabases();
    }

    // Database not selected
    @Test
    public void insertDatabaseNotSelected() throws SQLException {
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (null)", session);
        });
        assertEquals("No database selected", e.getMessage());
    }


    // Table not exist
    @Test
    public void insertTableNotExist() throws SQLException {
        global.execute("create database test; use test", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (null)", session);
        });
        assertEquals("Table does not exist", e.getMessage());
    }

    // Violate not null
    @Test
    public void insertViolateNotNull() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int not null)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (null)", session);
        });
        assertEquals("Violate not null constraint", e.getMessage());
        global.execute("create table test2 (i int not null, l long)", session);
        e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test2 (l) values (0)", session);
        });
        assertEquals("Violate not null constraint", e.getMessage());
    }

    // Multiple insert
    @Test
    public void insertMultiple() throws SQLException {
        Table result;
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, s string)", session);
        result = global.execute("insert into test (s, i) values (\"\", -1e3)", session);
        assertEquals(new Table("RESULT",
                new Column[] { new Column("INSERTED", Type.INT) },
                new Object[][] {
                        new Object[] { 1 }
                }
        ), result);
        result = global.execute("insert into test values (+-+-1, ''),  (+-+-1, ''), (null, \"hello\\t world\")", session);
        assertEquals(new Table("RESULT",
                new Column[] { new Column("INSERTED", Type.INT) },
                new Object[][] {
                        new Object[] { 3 }
                }
        ), result);
        Table copy = new Global(Config.root).getDatabase("TEST").getTable("TEST");
        assertEquals(new Table("TEST",
                new Column[] { new Column("I", Type.INT), new Column("S", Type.STRING) },
                new Object[][] {
                        new Object[] { -1000, "" },
                        new Object[] { 1, "" },
                        new Object[] { 1, "" },
                        new Object[] { null, "HELLO\t WORLD" }
                }
        ), copy);
    }

    // Length mismatch
    @Test
    public void insertLengthMismatch() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j long)", session);
        Exception e = assertThrows(SQLException.class, () -> {
                global.execute("insert into test values (1.0)", session);
        });
        assertEquals("Row size mismatch", e.getMessage());
    }

    // Violate primary key
    @Test
    public void insertViolatePrimaryKey() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j long, primary key (i, j))", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (1, 1), (1, 1)", session);
        });
        assertEquals("Violate primary key constraint", e.getMessage());
        e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test values (null, 1)", session);
        });
        assertEquals("Violate not null constraint", e.getMessage());
    }

    // Type conversion
    @Test
    public void insertTypeConversion() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, l long, f float, d double, s string)", session);
        global.execute("insert into test values (-1, -1, -1, -1, -1)", session);
        global.execute("insert into test values (2.0, 2.0, 2.0, 2.0, 2.0)", session);
        Table copy = new Global(Config.root).getDatabase("TEST").getTable("TEST");
        assertEquals(new Table("TEST",
                new Column[] {
                        new Column("I", Type.INT), new Column("L", Type.LONG),
                        new Column("F", Type.FLOAT), new Column("D", Type.DOUBLE),
                        new Column("S", Type.STRING)
                },
                new Object[][] {
                        new Object[] {-1, -1L, -1.0f, -1.0, "-1"},
                        new Object[] {2, 2L, 2.0f, 2.0, "2.0"}
                }
        ), copy);
    }

    // Unknown column name
    @Test
    public void insertUnknownColumnName() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("insert into test(n) values (1)", session);
        });
        assertEquals("Unknown column name", e.getMessage());
    }

    // Length mismatch exception should be atomic. Won't fix.
    // Column name occurs twice should be error. Won't fix.
}
