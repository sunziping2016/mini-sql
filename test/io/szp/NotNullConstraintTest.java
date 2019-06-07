package io.szp;

import io.szp.exception.SQLException;
import io.szp.schema.Global;
import io.szp.schema.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NotNullConstraintTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();

        global.removeAllDatabases();
    }

    // 更新时插入null
    @Test
    public void NotNullConstraintUpdateRow() throws SQLException {
        global.execute("create database test; use test", session);
        global.execute("create table test (i int, j long not null)", session);
        global.execute("insert into test values (1, 2), (1, 3)", session);
        Exception e = assertThrows(SQLException.class, () -> {
            global.execute("update test set j = null where j = 3", session);
        });
        assertEquals("Violate not null constraint", e.getMessage());
    }
}
