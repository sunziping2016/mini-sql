grammar SQL;

// From https://github.com/antlr/grammars-v4/blob/master/mysql/MySqlParser.g4

root
    : statements? EOF
    ;

statements
    : (statement SEMI | SEMI)* (statement (SEMI)? | SEMI)
    ;

statement
    : createDatabase    # CreateDatabaseStatement
    | createTable       # CreateTableStatement
    | dropDatabase      # DropDatabaseStatement
    | dropTable         # DropTableStatement
    | selectStatement   # SelectStatementStatement
    | insertStatement   # InsertStatementStatement
    | updateStatement   # UpdateStatementStatement
    | deleteStatement   # DeleteStatementStatement
    | useStatement      # UseStatementStatement
    | showDatabases     # ShowDatabasesStatement
    | showTables        # ShowTablesStatement
    | importStatement   # ImportStatementStatement
    ;

createDatabase
    : CREATE DATABASE uid
    ;

dropDatabase
    : DROP DATABASE uid
    ;

createTable
    : CREATE TABLE uid '(' createDefinition (',' createDefinition)* ')'
    ;

dropTable
    : DROP TABLE uid
    ;

createDefinition
    : uid typeName=(
        INT | LONG | FLOAT | DOUBLE | STRING
      ) columnConstraint*                       # CreateDefinitionAddColumn
    | PRIMARY KEY '(' uidList ')'               # CreateDefinitionPrimaryKeyConstraint
    ;

columnConstraint
    : NOT NULL      # ColumnConstraintNotNull
    | PRIMARY KEY   # ColumnConstraintPrimaryKey
    ;

showDatabases
    : SHOW DATABASES
    ;

showTables
    : SHOW TABLES
    ;

selectStatement
    : SELECT selectElements FROM tableSources (WHERE expression)?
    ;

selectElements
    : star='*'
    | selectElement (',' selectElement)*
    ;

selectElement
    : fullColumnName (AS uid)
    ;

tableSources
    : tableSource (',' tableSource)*
    ;

tableSource
    : uid joinPart*
    ;

joinPart
    : INNER? JOIN uid (ON expression)?
    | (LEFT | RIGHT) OUTER? JOIN uid (ON expression)?
    ;

insertStatement
    : INSERT INTO uid ('(' columns=uidList ')')? VALUES '(' expressions')' (',' '(' expressions ')')*
    ;

updateStatement
    : UPDATE uid SET updatedElement (',' updatedElement)* (WHERE expression)?
    ;

updatedElement
    :  uid '=' expression
    ;

deleteStatement
    : DELETE FROM uid (WHERE expression)?
    ;

useStatement
    : USE uid
    ;

uid
    : ID
    ;
    
fullColumnName
    : (uid '.')? uid
    ;

uidList
    : uid (',' uid)*
    ;

expression
    : notOperator=(NOT | '!') expression                    # NotExpression
    | left=expression logicalOperator right=expression      # LogicalExpression
    | predicate IS NOT? testValue=(TRUE | FALSE | UNKNOWN)  # IsBooleanExpression
    | predicate                                             # NestedPredicateExpression
    ;

expressions
    : expression (',' expression)*
    ;

predicate
    : left=predicate comparisonOperator right=predicate # CompareExpression
    | predicate IS NOT? NULL                            # IsNullExpression
    | expressionAtom                                    # NestedAtomExpression
    ;

expressionAtom
    : constant                              # ConstantExpression
    | (tableName=uid '.') ? columnName=uid  # VariableExpression
    | unaryOperator expressionAtom          # UnaryExpression
    | '(' expression ')'                    # NestedExpression
    ;

constant
    : STRING_LITERAL    # StringConstant
    | DECIMAL_LITERAL   # DecimalConstant
    | REAL_LITERAL      # RealConstant
    | TRUE              # TrueConstant
    | FALSE             # FalseConstant
    | NULL              # NullConstant
    ;

unaryOperator
    : '!' | '+' | '-' | NOT
    ;


logicalOperator
    : AND | '&' '&' | OR | '|' '|'
    ;

comparisonOperator
    : '='               # EqualOperator
    | '>'               # GreateThanOperator
    | '<'               # LessThanOperator
    | '<' '='           # LessEqualOperator
    | '>' '='           # GreatEqualOperator
    | '<' '>'           # NotEqualOperator
    | '!' '='           # NotEqual2Operator
    ;

importStatement
    : IMPORT STRING_LITERAL
    ;

SPACE:                               [ \t\r\n]+ -> skip;

// keywords
CREATE:                              'CREATE';
DROP:                                'DROP';
DATABASE:                            'DATABASE';
TABLE:                               'TABLE';
SELECT:                              'SELECT';
AS:                                  'AS';
FROM:                                'FROM';
WHERE:                               'WHERE';
INNER:                               'INNER';
JOIN:                                'JOIN';
ON:                                  'ON';
LEFT:                                'LEFT';
RIGHT:                               'RIGHT';
OUTER:                               'OUTER';
NOT:                                 'NOT';
AND:                                 'AND';
OR:                                  'OR';
INSERT:                              'INSERT';
INTO:                                'INTO';
VALUES:                              'VALUES';
UPDATE:                              'UPDATE';
SET:                                 'SEpredicate IS NOT? testValue=(TRUE | FALSE | UNKNOWN) T';
DELETE:                              'DELETE';
USE:                                 'USE';
IS:                                  'IS';
TRUE:                                'TRUE';
FALSE:                               'FALSE';
UNKNOWN:                             'UNKNOWN';
SHOW:                                'SHOW';
DATABASES:                           'DATABASES';
TABLES:                              'TABLES';

INT:                                 'INT';
LONG:                                'LONG';
FLOAT:                               'FLOAT';
DOUBLE:                              'DOUBLE';
STRING:                              'STRING';

NULL:                                'NULL';
PRIMARY:                             'PRIMARY';
KEY:                                 'KEY';

// custom
IMPORT:                              'IMPORT';

// operators
SEMI:                                ';';

// literals
STRING_LITERAL:                      DQUOTA_STRING | SQUOTA_STRING;
DECIMAL_LITERAL:                     DEC_DIGIT+;

REAL_LITERAL:                        (DEC_DIGIT+)? '.' DEC_DIGIT+
                                     | DEC_DIGIT+ '.' EXPONENT_NUM_PART
                                     | (DEC_DIGIT+)? '.' (DEC_DIGIT+ EXPONENT_NUM_PART)
                                     | DEC_DIGIT+ EXPONENT_NUM_PART;

// identifiers
ID:                                  ID_LITERAL;

fragment EXPONENT_NUM_PART:          'E' '-'? DEC_DIGIT+;
fragment ID_LITERAL:                 [A-Z_$0-9]*?[A-Z_$]+?[A-Z_$0-9]*;
fragment DQUOTA_STRING:              '"' ( '\\'. | ~('\\') )* '"';
fragment SQUOTA_STRING:              '\'' ( '\\'. | ~('\\') )* '\'';
fragment DEC_DIGIT:                  [0-9];