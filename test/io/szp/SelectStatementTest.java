package io.szp;

import io.szp.exception.SQLException;
import io.szp.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelectStatementTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();
        global.removeAllDatabases();
    }

    @Test
    public void selectJoin() throws SQLException {
        //测试join on
        Table result;
//        创建test表并插入数据
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j int, s string)", session);
        global.execute("insert into test values (+-+-1, 1,''),  (+-+-1,2, ''), (null, 3,\"hello\\t world\")", session);

//        创建test表2并插入数据
        global.execute("create table test2 (k int, j int, s2 string)", session);
        global.execute("insert into test2 values (+-+-1,2, ''),  (+-+-1,3, ''), (null,4, \"hello\\t world\")", session);

        result = global.execute("select `test join test2`.i as new_col1,s,`test join test2`.s2 from test join test2 on test.j = test2.j ", session);
        assertEquals(new Table("RESULT",
                new Column[] {
                        new Column("NEW_COL1", Type.INT),
                        new Column("S", Type.STRING),
                        new Column("S2", Type.STRING)
                },
                new Object[][] {
                        new Object[] {1, "", ""},
                        new Object[] {null, "HELLO\t WORLD", ""}
                }
        ), result);
    }
}
