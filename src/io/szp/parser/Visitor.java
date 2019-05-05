package io.szp.parser;

import io.szp.schema.Column;
import io.szp.schema.Type;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Visitor extends SQLBaseVisitor<Object> {
    private enum ColumnConstraint {
        NOT_NULL,
        PRIMARY_KEY
    }

    @Override
    public ArrayList<Statement> visitRoot(SQLParser.RootContext ctx) {
        var statements = ctx.statements();
        if (statements == null)
            return new ArrayList<>();
        return (ArrayList<Statement>) visit(statements);
    }

    @Override
    public ArrayList<Statement> visitStatements(SQLParser.StatementsContext ctx) {
        ArrayList<Statement> statements = new ArrayList<>();
        for (var statement : ctx.statement())
            statements.add((Statement) visit(statement));
        return statements;
    }

    @Override
    public Object visitCreateDatabaseStatement(SQLParser.CreateDatabaseStatementContext ctx) {
        return visit(ctx.createDatabase());
    }

    @Override
    public Object visitCreateTableStatement(SQLParser.CreateTableStatementContext ctx) {
        return visit(ctx.createTable());
    }

    @Override
    public Object visitDropDatabaseStatement(SQLParser.DropDatabaseStatementContext ctx) {
        return visit(ctx.dropDatabase());
    }

    @Override
    public Object visitDropTableStatement(SQLParser.DropTableStatementContext ctx) {
        return visit(ctx.dropTable());
    }

    @Override
    public Object visitShowDatabasesStatement(SQLParser.ShowDatabasesStatementContext ctx) {
        return visit(ctx.showDatabases());
    }

    @Override
    public Object visitShowTablesStatement(SQLParser.ShowTablesStatementContext ctx) {
        return visit(ctx.showTables());
    }

    @Override
    public Object visitSelectStatementStatement(SQLParser.SelectStatementStatementContext ctx) {
        return visit(ctx.selectStatement());
    }

    @Override
    public Object visitInsertStatementStatement(SQLParser.InsertStatementStatementContext ctx) {
        return visit(ctx.insertStatement());
    }

    @Override
    public Object visitUpdateStatementStatement(SQLParser.UpdateStatementStatementContext ctx) {
        return visit(ctx.updateStatement());
    }

    @Override
    public Object visitDeleteStatementStatement(SQLParser.DeleteStatementStatementContext ctx) {
        return visit(ctx.deleteStatement());
    }

    @Override
    public Object visitUseStatementStatement(SQLParser.UseStatementStatementContext ctx) {
        return visit(ctx.useStatement());
    }

    @Override
    public Object visitImportStatementStatement(SQLParser.ImportStatementStatementContext ctx) {
        return visit(ctx.importStatement());
    }

    @Override
    public Statement visitCreateDatabase(SQLParser.CreateDatabaseContext ctx) {
        return new CreateDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Statement visitCreateTable(SQLParser.CreateTableContext ctx) {
        ArrayList<CreateTableDefinition> definitions = new ArrayList<>();
        for (var definition : ctx.createDefinition())
            definitions.add((CreateTableDefinition) visit(definition));
        return new CreateTableStatement((String) visit(ctx.uid()), definitions);
    }

    @Override
    public Statement visitDropDatabase(SQLParser.DropDatabaseContext ctx) {
        return new DropDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Statement visitDropTable(SQLParser.DropTableContext ctx) {
        return new DropTableStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitShowDatabases(SQLParser.ShowDatabasesContext ctx) {
        return new ShowDatabasesStatement();
    }

    @Override
    public Object visitShowTables(SQLParser.ShowTablesContext ctx) {
        return new ShowTablesStatement();
    }

    @Override
    public Statement visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Statement visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Statement visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Statement visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Statement visitUseStatement(SQLParser.UseStatementContext ctx) {
        return new UseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Statement visitImportStatement(SQLParser.ImportStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Object visitUid(SQLParser.UidContext ctx) {
        return ctx.ID().getSymbol().getText();
    }

    @Override
    public ArrayList<String> visitUidList(SQLParser.UidListContext ctx) {
        ArrayList<String> uids = new ArrayList<>();
        for (var uid : ctx.uid())
            uids.add((String) visit(uid));
        return uids;
    }

    @Override
    public CreateTableDefinitionAddColumn visitCreateDefinitionAddColumn(SQLParser.CreateDefinitionAddColumnContext ctx) {
        String name =(String) visit(ctx.uid());
        Type type;
        boolean is_not_null = false, is_primary_key = false;
        String type_text = ctx.typeName.getText();
        switch (type_text) {
            case "INT":
                type = Type.INT;
                break;
            case "LONG":
                type = Type.LONG;
                break;
            case "FLOAT":
                type = Type.FLOAT;
                break;
            case "DOUBLE":
                type = Type.DOUBLE;
                break;
            case "STRING":
                type = Type.STRING;
                break;
            default:
                throw new RuntimeException("Unknown type in table definition statement");
        }
        for (var constraint : ctx.columnConstraint()) {
            switch ((ColumnConstraint) visit(constraint)) {
                case NOT_NULL:
                    is_not_null = true;
                    break;
                case PRIMARY_KEY:
                    is_primary_key = true;
                    is_not_null = true;
                    break;
            }
        }
        return new CreateTableDefinitionAddColumn(new Column(name, type, is_not_null, is_primary_key));
    }

    @Override
    public CreateTableDefinitionPrimaryKey visitCreateDefinitionPrimaryKeyConstraint(
            SQLParser.CreateDefinitionPrimaryKeyConstraintContext ctx) {
        return new CreateTableDefinitionPrimaryKey((ArrayList<String>) visit(ctx.uidList()));
    }

    @Override
    public ColumnConstraint visitColumnConstraintNotNull(SQLParser.ColumnConstraintNotNullContext ctx) {
        return ColumnConstraint.NOT_NULL;
    }

    @Override
    public ColumnConstraint visitColumnConstraintPrimaryKey(SQLParser.ColumnConstraintPrimaryKeyContext ctx) {
        return ColumnConstraint.PRIMARY_KEY;
    }
}
