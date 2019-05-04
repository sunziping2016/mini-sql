package io.szp.parser;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Visitor extends SQLBaseVisitor<Object> {
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
        for (var statement: ctx.statement())
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
    public Object visitCreateTable(SQLParser.CreateTableContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitDropDatabase(SQLParser.DropDatabaseContext ctx) {
        return new DropDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitDropTable(SQLParser.DropTableContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitUseStatement(SQLParser.UseStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitImportStatement(SQLParser.ImportStatementContext ctx) {
        return new Statement();
    }

    @Override
    public Object visitUid(SQLParser.UidContext ctx) {
        return ctx.ID().getSymbol().getText();
    }
}
