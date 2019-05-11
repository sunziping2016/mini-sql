package io.szp.parser;

import io.szp.expression.*;
import io.szp.schema.Column;
import io.szp.schema.Type;
import io.szp.statement.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Visitor extends SQLBaseVisitor<Object> {
    private enum ColumnConstraint {
        NOT_NULL,
        PRIMARY_KEY
    }

    @Override
    public Object visitRoot(SQLParser.RootContext ctx) {
        SQLParser.StatementsContext statements = ctx.statements();
        if (statements == null)
            return new ArrayList<>();
        return visit(statements);
    }

    @Override
    public Object visitStatements(SQLParser.StatementsContext ctx) {
        ArrayList<Statement> statements = new ArrayList<>();
        for (SQLParser.StatementContext statement : ctx.statement())
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
    public Object visitCreateDatabase(SQLParser.CreateDatabaseContext ctx) {
        return new CreateDatabaseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitCreateTable(SQLParser.CreateTableContext ctx) {
        ArrayList<CreateTableDefinition> definitions = new ArrayList<>();
        for (SQLParser.CreateDefinitionContext definition : ctx.createDefinition())
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
        ArrayList<TableSource> table_sources = new ArrayList<>();
        Expression expression = null;
        for (SQLParser.TableSourceContext table_source : ctx.tableSource())
            table_sources.add((TableSource) visit(table_source));
        if (ctx.expression() != null)
            expression = (Expression) visit(ctx.expression());
        return new SelectStatement(
                (ArrayList<SelectStatement.SelectElement>) visit(ctx.selectElements()),
                table_sources,
                expression
        );
    }

    @Override
    public Object visitEmptySelectElements(SQLParser.EmptySelectElementsContext ctx) {
        return null;
    }

    @Override
    public Object visitNonemptySelectElements(SQLParser.NonemptySelectElementsContext ctx) {
        ArrayList<SelectStatement.SelectElement> select_elements = new ArrayList<>();
        for (SQLParser.SelectElementContext select_element : ctx.selectElement())
            select_elements.add((SelectStatement.SelectElement) visit(select_element));
        return select_elements;
    }

    @Override
    public Object visitSelectElement(SQLParser.SelectElementContext ctx) {
        String alias = null;
        if (ctx.uid() != null)
            alias = (String) visit(ctx.uid());
        return new SelectStatement.SelectElement((FullColumnName) visit(ctx.fullColumnName()), alias);
    }

    @Override
    public Object visitTableSource(SQLParser.TableSourceContext ctx) {
        ArrayList<TableSource.JoinPart> joins_parts = new ArrayList<>();
        for (SQLParser.JoinPartContext join : ctx.joinPart())
            joins_parts.add((TableSource.JoinPart) visit(join));
        String alias = null;
        if (ctx.alias != null)
            alias = (String) visit(ctx.alias);
        return new TableSource((String) visit(ctx.table), joins_parts, alias);
    }

    @Override
    public Object visitJoinPart(SQLParser.JoinPartContext ctx) {
        Expression expression = null;
        if (ctx.expression() != null)
            expression = (Expression) visit(ctx.expression());
        return new TableSource.JoinPart((String) visit(ctx.uid()), expression);
    }

    @Override
    public Object visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        ArrayList<String> column_list = null;
        if (ctx.uidList() != null)
            column_list = (ArrayList<String>) visit(ctx.uidList());
        ArrayList<ArrayList<Expression>> data = new ArrayList<>();
        for (SQLParser.ExpressionsContext row : ctx.expressions())
            data.add((ArrayList<Expression>) visit(row));
        return new InsertStatement(
                (String) visit(ctx.uid()),
                column_list,
                data
        );
    }

    @Override
    public Object visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        ArrayList<UpdateStatement.UpdatedElement> updated_elements = new ArrayList<>();
        for (SQLParser.UpdatedElementContext updated_element : ctx.updatedElement())
            updated_elements.add((UpdateStatement.UpdatedElement) visit(updated_element));
        Expression expression = null;
        if (ctx.expression() != null)
            expression = (Expression) visit(ctx.expression());
        return new UpdateStatement(
                (String) visit(ctx.uid()),
                updated_elements,
                expression
        );
    }

    @Override
    public Object visitUpdatedElement(SQLParser.UpdatedElementContext ctx) {
        return new UpdateStatement.UpdatedElement(
                (String) visit(ctx.uid()),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Object visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        Expression expression = null;
        if (ctx.expression() != null)
            expression = (Expression) visit(ctx.expression());
        return new DeleteStatement((String) visit(ctx.uid()), expression);
    }

    @Override
    public Object visitUseStatement(SQLParser.UseStatementContext ctx) {
        return new UseStatement((String) visit(ctx.uid()));
    }

    @Override
    public Object visitUid(SQLParser.UidContext ctx) {
        return ctx.ID().getSymbol().getText();
    }

    @Override
    public ArrayList<String> visitUidList(SQLParser.UidListContext ctx) {
        ArrayList<String> uids = new ArrayList<>();
        for (SQLParser.UidContext uid : ctx.uid())
            uids.add((String) visit(uid));
        return uids;
    }

    @Override
    public Object visitFullColumnName(SQLParser.FullColumnNameContext ctx) {
        String table_name = null;
        if (ctx.tableName != null)
            table_name = (String) visit(ctx.tableName);
        return new FullColumnName(table_name, (String) visit(ctx.columnName));
    }

    @Override
    public CreateTableDefinitionAddColumn visitCreateDefinitionAddColumn(
            SQLParser.CreateDefinitionAddColumnContext ctx) {
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
        for (SQLParser.ColumnConstraintContext constraint : ctx.columnConstraint()) {
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
    public Object visitExpressions(SQLParser.ExpressionsContext ctx) {
        ArrayList<Expression> expressions = new ArrayList<>();
        for (SQLParser.ExpressionContext expression : ctx.expression())
            expressions.add((Expression) visit(expression));
        return expressions;
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
        return new VariableExpression((FullColumnName) visit(ctx.fullColumnName()));
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
