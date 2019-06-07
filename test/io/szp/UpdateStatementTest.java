package io.szp;

import io.szp.exception.SQLException;
import io.szp.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
