package io.szp.minisql;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Column;
import io.szp.minisql.schema.Global;
import io.szp.minisql.schema.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteStatementTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();
        global.removeAllDatabases();
    }

    @Test
    public void deleteWhere() throws SQLException {
        Table result;
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, s string)", session);
        global.execute("insert into test values (+-+-1, '11'),  (+-+-2, '22'), (+-+-2, '3'),(null, \"hello\\t world\")", session);
        result = global.execute("delete from test where s=\"hello\\t world\"", session);
        assertEquals(new Table("RESULT",
                new Column[] { new Column("DELETED", Type.INT) },
                new Object[][] {
                        new Object[] { 1 }
                }
        ), result);

//        删除多条记录
        result = global.execute("delete from test where i=2", session);
        assertEquals(new Table("RESULT",
                new Column[] { new Column("DELETED", Type.INT) },
                new Object[][] {
                        new Object[] { 2 }
                }
        ), result);
    }
}
