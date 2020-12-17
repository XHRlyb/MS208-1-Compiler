grammar Mx;

program : subprogram* EOF;

wow1 : subprogram;

wow2 : subprogram*;

wow3 : subprogram Dot subprogram;

subprogram : varDef | funDef | classDef;

classDef : Class Identifier '{' (varDef | funDef)* '}' ';';
funDef : type? Identifier '(' paraLis? ')' suite;
varDef : type singleVarDef (',' singleVarDef)* ';';

singleVarDef : Identifier ('=' expression)?;

paraLis : para (',' para)*;
para : type Identifier;

basType : Int | Bool | String;
type : (Identifier | basType) ('[' ']')* | Void;

suite : '{' statement* '}';

statement
    : suite                                                 #block
    | varDef                                                #vardefStmt
    | If '(' expression ')' trueStmt=statement
        (Else falseStmt=statement)?                         #ifStmt
    | For '(' init=expression? ';' cond=expression? ';'
                incr=expression? ')' statement              #forStmt
    | While '(' expression ')' statement                    #whileStmt
    | Return expression? ';'                                #returnStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt
    | expression ';'                                        #pureExprStmt
    | ';'                                                   #emptyStmt
    ;

literal : Integer | StringLiteral | boolValue=(True | False) | Null;

primExp : '(' expression ')' | This | Identifier | literal;

creator
    : (basType | Identifier) ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+ #errorCreator
    | (basType | Identifier) ('[' expression ']')+ ('[' ']')* #arrayCreator
    | (basType | Identifier) '(' ')'                          #classCreator
    | (basType | Identifier)                                  #basicCreator
    ;

expressionLis : expression (',' expression)*;

expression
    : primExp                                               #atomExpr
    | expression '.' Identifier                             #memberExpr
    | <assoc=right> 'new' creator                           #newExpr
    | expression '[' expression ']'                         #subscript
    | expression '(' expressionLis? ')'                    #funcCall
    | expression op=('++' | '--')                           #suffixExpr
    | <assoc=right> op=('+' | '-' | '++' | '--') expression #prefixExpr
    | <assoc=right> op=('~' | '!' ) expression              #prefixExpr
    | expression op=('*' | '/' | '%') expression            #binaryExpr
    | expression op=('+' | '-') expression                  #binaryExpr
    | expression op=('<<' | '>>') expression                #binaryExpr
    | expression op=('<' | '>' | '>=' | '<=') expression    #binaryExpr
    | expression op=('==' | '!=' ) expression               #binaryExpr
    | expression op='&' expression                          #binaryExpr
    | expression op='^' expression                          #binaryExpr
    | expression op='|' expression                          #binaryExpr
    | expression '&&' expression                            #binaryExpr
    | expression '||' expression                            #binaryExpr
    | <assoc=right> expression '=' expression               #assignExpr
    ;

Int : 'int';
Bool : 'bool';
String : 'string';
Null : 'null';
Void : 'void';
True : 'true';
False : 'false';
If : 'if';
Else : 'else';
For : 'for';
While : 'while';
Break : 'break';
Continue : 'continue';
Return : 'return';
New : 'new';
Class : 'class';
This : 'this';

Plu : '+';
Sub : '-';
Mul : '*';
Div : '/';
Mod : '%';

Gt : '>';
Lt : '<';
Ge : '>=';
Le : '<=';
Eq : '==';
Neq : '!=';

AndAnd : '&&';
OrOr : '||';
Not : '!';

RShift : '>>';
LShift : '<<';
And : '&';
Or : '|';
Xor : '^';
Conty : '~';

Assign : '=';

PluPlu : '++';
SubSub : '--';

Dot : '.';

LParen : '(';
RParen : ')';
LBracket : '[';
RBracket : ']';
LBrace : '{';
RBrace : '}';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

StringLiteral : '"' SChar* '"';

fragment

SChar : ~["\\\n\r] | '\\n' | '\\\\' | '\\"';

Identifier : [a-zA-Z] [a-zA-Z_0-9]*;

Integer : [1-9] [0-9]* | '0';

Whitespace : [ \t]+ -> skip ;

Newline : ('\r' '\n' ? | '\n') -> skip;

LineComment : '//' ~[\r\n]* -> skip;