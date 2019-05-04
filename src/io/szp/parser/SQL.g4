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
    | importStatement   # ImportStatementStatement
    ;

createDatabase
    : CREATE DATABASE uid
    ;

createTable
    : CREATE TABLE uid createDefinitions
    ;

dropDatabase
    : DROP DATABASE uid
    ;

dropTable
    : DROP TABLE uid
    ;

createDefinitions
    : '(' createDefinition (',' createDefinition)* ')'
    ;

createDefinition
    : uid columnDefinition
    | tableConstraint
    ;

columnDefinition
    : typeName=(
        INT | LONG | FLOAT | DOUBLE | STRING
      ) columnConstraint*
    ;

columnConstraint
    : NOT NULL
    | PRIMARY KEY
    ;

tableConstraint
    : PRIMARY KEY indexColumnNames
    ;

indexColumnNames
    : '(' uidList ')'
    ;

selectStatement
    : SELECT selectElements fromClause?
    ;

selectElements
    : star='*'
    | selectElement (',' selectElement)*
    ;

selectElement
    : fullColumnName (AS uid)
    ;

fromClause
    : FROM tableSources (WHERE expression)?
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
    : INSERT INTO uid ('(' columns=uidList ')')? insertStatementValue
    ;

insertStatementValue
    : VALUES '(' expressions')' (',' '(' expressions ')')*
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
    : notOperator=(NOT | '!') expression
    | expression logicalOperator expression
    | predicate
    ;

expressions
    : expression (',' expression)*
    ;

predicate
    : left=predicate comparisonOperator right=predicate
    | expressionAtom
    ;

expressionAtom
    : constant
    | (uid '.') ? uid
    | '(' expression ')'
    ;

constant
    : STRING_LITERAL | '-'? DECIMAL_LITERAL | '-'? REAL_LITERAL | NULL
    ;

logicalOperator
    : AND | '&' '&' | OR | '|' '|'
    ;

comparisonOperator
    : '=' | '>' | '<' | '<' '=' | '>' '='
    | '<' '>' | '!' '='
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
SET:                                 'SET';
DELETE:                              'DELETE';
USE:                                 'USE';

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
fragment DQUOTA_STRING:              '"' ( '\\'. | '""' | ~('"'| '\\') )* '"';
fragment SQUOTA_STRING:              '\'' ('\\'. | '\'\'' | ~('\'' | '\\'))* '\'';
fragment DEC_DIGIT:                  [0-9];