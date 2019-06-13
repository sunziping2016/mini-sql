package io.szp.minisql;

import io.szp.minisql.exception.SQLException;
import io.szp.minisql.schema.Global;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateStatementTest {
    private Global global;
    private Session session;

    @BeforeEach
    public void init() throws SQLException {
        global = new Global(Config.root);
        session = new Session();
        global.removeAllDatabases();
    }

    @Test
    public void updateSet() throws SQLException {

    }

}
