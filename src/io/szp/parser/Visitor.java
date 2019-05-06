package io.szp.parser;

import io.szp.expression.*;
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
    public Object visitRoot(SQLParser.RootContext ctx) {
        var statements = ctx.statements();
        if (statements == null)
            return new ArrayList<>();
        return visit(statements);
    }

    @Override
    public Object visitStatements(SQLParser.StatementsContext ctx) {
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
    public Object visitCreateDatabase(SQLParser.CreateDatabaseContext ctx) {
        return new CreateDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitCreateTable(SQLParser.CreateTableContext ctx) {
        ArrayList<CreateTableDefinition> definitions = new ArrayList<>();
        for (var definition : ctx.createDefinition())
            definitions.add((CreateTableDefinition) visit(definition));
        return new CreateTableStatement((String) visit(ctx.uid()), definitions);
    }

    @Override
    public Object visitDropDatabase(SQLParser.DropDatabaseContext ctx) {
        return new DropDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitDropTable(SQLParser.DropTableContext ctx) {
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
    public Object visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        Expression expression;
        if (ctx.expression() != null)
            expression = (Expression) visit(ctx.expression());
        else
            expression = new BooleanConstant(true);
        return new SelectStatement(expression);
    }

    @Override
    public Object visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Object visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Object visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        return new EmptyStatement();
    }

    @Override
    public Object visitUseStatement(SQLParser.UseStatementContext ctx) {
        return new UseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitImportStatement(SQLParser.ImportStatementContext ctx) {
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

    @Override
    public Object visitNotExpression(SQLParser.NotExpressionContext ctx) {
        return new UnaryExpression(UnaryExpression.Operator.NOT, (Expression) visit (ctx.expression()));
    }

    @Override
    public Object visitLogicalExpression(SQLParser.LogicalExpressionContext ctx) {
        return new LogicalExpression(
                (Expression) visit(ctx.left),
                (LogicalExpression.Operator) visit(ctx.logicalOperator()),
                (Expression) visit(ctx.right)
        );
    }

    @Override
    public Object visitIsBooleanExpression(SQLParser.IsBooleanExpressionContext ctx) {
        Boolean bool = null;
        switch (ctx.testValue.getText()) {
            case "TRUE":
                bool = true;
                break;
            case "FALSE":
                bool = false;
                break;
        }
        return new IsBooleanExpression((Expression) visit(ctx.predicate()), bool,ctx.NOT() != null);
    }

    @Override
    public Object visitNestedPredicateExpression(SQLParser.NestedPredicateExpressionContext ctx) {
        return visit(ctx.predicate());
    }

    @Override
    public Object visitCompareExpression(SQLParser.CompareExpressionContext ctx) {
        return new CompareExpression(
                (Expression) visit(ctx.left),
                (CompareExpression.Operator) visit(ctx.comparisonOperator()),
                (Expression) visit(ctx.right)
        );
    }

    @Override
    public Object visitIsNullExpression(SQLParser.IsNullExpressionContext ctx) {
        return new IsNullExpression((Expression) visit(ctx.predicate()), ctx.NOT() != null);
    }

    @Override
    public Object visitNestedAtomExpression(SQLParser.NestedAtomExpressionContext ctx) {
        return visit(ctx.expressionAtom());
    }

    @Override
    public Object visitConstantExpression(SQLParser.ConstantExpressionContext ctx) {
        return visit(ctx.constant());
    }

    @Override
    public Object visitVariableExpression(SQLParser.VariableExpressionContext ctx) {
        String table_name = null;
        if (ctx.tableName != null)
            table_name = (String) visit(ctx.tableName);
        return new VariableExpression(table_name, (String) visit(ctx.columnName));
    }

    @Override
    public Object visitUnaryExpression(SQLParser.UnaryExpressionContext ctx) {
        UnaryExpression.Operator operator = UnaryExpression.Operator.POSITIVE;
        switch (ctx.unaryOperator().getText()) {
            case "!": case "NOT":
                operator = UnaryExpression.Operator.NOT;
                break;
            case "-":
                operator = UnaryExpression.Operator.NEGATIVE;
                break;
        }
        return new UnaryExpression(operator, (Expression) visit(ctx.expressionAtom()));
    }

    @Override
    public Object visitNestedExpression(SQLParser.NestedExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitStringConstant(SQLParser.StringConstantContext ctx) {
        return new StringConstant(ctx.getText());
    }

    @Override
    public Object visitDecimalConstant(SQLParser.DecimalConstantContext ctx) {
        return new DecimalConstant(ctx.getText());
    }

    @Override
    public Object visitRealConstant(SQLParser.RealConstantContext ctx) {
        return new RealConstant(ctx.getText());
    }

    @Override
    public Object visitTrueConstant(SQLParser.TrueConstantContext ctx) {
        return new BooleanConstant(true);
    }

    @Override
    public Object visitFalseConstant(SQLParser.FalseConstantContext ctx) {
        return new BooleanConstant(false);
    }

    @Override
    public Object visitNullConstant(SQLParser.NullConstantContext ctx) {
        return new NullConstant();
    }

    @Override
    public Object visitAndOperator(SQLParser.AndOperatorContext ctx) {
        return LogicalExpression.Operator.AND;
    }

    @Override
    public Object visitAnd2Operator(SQLParser.And2OperatorContext ctx) {
        return LogicalExpression.Operator.AND;
    }

    @Override
    public Object visitOrOperator(SQLParser.OrOperatorContext ctx) {
        return LogicalExpression.Operator.OR;
    }

    @Override
    public Object visitOr2Operator(SQLParser.Or2OperatorContext ctx) {
        return LogicalExpression.Operator.OR;
    }

    @Override
    public Object visitEqualOperator(SQLParser.EqualOperatorContext ctx) {
        return CompareExpression.Operator.EQUAL;
    }

    @Override
    public Object visitGreateThanOperator(SQLParser.GreateThanOperatorContext ctx) {
        return CompareExpression.Operator.GREAT_THAN;
    }

    @Override
    public Object visitLessThanOperator(SQLParser.LessThanOperatorContext ctx) {
        return CompareExpression.Operator.LESS_THAN;
    }

    @Override
    public Object visitLessEqualOperator(SQLParser.LessEqualOperatorContext ctx) {
        return CompareExpression.Operator.LESS_EQUAL;
    }

    @Override
    public Object visitGreatEqualOperator(SQLParser.GreatEqualOperatorContext ctx) {
        return CompareExpression.Operator.GREAT_EQUAL;
    }

    @Override
    public Object visitNotEqualOperator(SQLParser.NotEqualOperatorContext ctx) {
        return CompareExpression.Operator.NOT_EQUAL;
    }

    @Override
    public Object visitNotEqual2Operator(SQLParser.NotEqual2OperatorContext ctx) {
        return CompareExpression.Operator.NOT_EQUAL;
    }
}
