grammar Mx;

program : subprogram* EOF;
subprogram : varDef | funDef | classDef;

classDef : Class Identifier '{' (varDef | funDef)* '}' ';';
funDef : returnType? Identifier '(' paraLis? ')' block;
varDef : type varDefSig (',' varDefSig)* ';';
varDefSig : Identifier ('=' expression)?;

paraLis : para (',' para)*;
para : type Identifier;

returnType : type | Void;
basType : Int | Bool | String | Identifier;
type : basType ('[' ']')*;

block : '{' statement* '}';

statement
    : block                                                 #blockStmt
    | varDef                                                #varDefStmt
    | If '(' expression ')' tStmt=statement
        (Else fStmt=statement)?                             #ifStmt
    | For '(' ini=expression? ';' cond=expression? ';'
                inc=expression? ')' statement               #forStmt
    | While '(' expression ')' statement                    #whileStmt
    | Return expression? ';'                                #returnStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt
    | expression ';'                                        #exprStmt
    | ';'                                                   #emptyStmt
    ;

literal : NullLiteral | BoolLiteral | IntLiteral | StringLiteral;

primExp : '(' expression ')' | This | Identifier | literal;

creator
    : basType ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+  #errorCreator
    | basType ('[' expression ']')+ ('[' ']')*                        #arrayCreator
    | basType '(' ')'                                                 #classCreator
    | basType                                                         #basicCreator
    ;

expressionLis : expression (',' expression)*;

expression
    : primExp                                                         #atomExpr
    | expression '.' Identifier                                       #memberExpr
    | <assoc=right> 'new' creator                                     #newExpr
    | bas=expression '[' offs=expression ']'                          #subscriptExpr
    | expression '(' expressionLis? ')'                               #funCallExpr
    | expression op=('++' | '--')                                     #suffixExpr
    | <assoc=right> op=('++' | '--') expression                       #prefixExpr
    | <assoc=right> op=('+' | '-' | '++' | '--') expression           #prefixExpr
    | <assoc=right> op=('~' | '!' ) expression                        #prefixExpr
    | src1=expression op=('*' | '/' | '%') src2=expression            #binaryExpr
    | src1=expression op=('+' | '-') src2=expression                  #binaryExpr
    | src1=expression op=('<<' | '>>') src2=expression                #binaryExpr
    | src1=expression op=('<' | '>' | '>=' | '<=') src2=expression    #binaryExpr
    | src1=expression op=('==' | '!=' ) src2=expression               #binaryExpr
    | src1=expression op='&' src2=expression                          #binaryExpr
    | src1=expression op='^' src2=expression                          #binaryExpr
    | src1=expression op='|' src2=expression                          #binaryExpr
    | src1=expression op='&&' src2=expression                         #binaryExpr
    | src1=expression op='||' src2=expression                         #binaryExpr
    | <assoc=right> src1=expression '=' src2=expression               #binaryExpr
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
IntLiteral : [1-9] [0-9]* | '0';
BoolLiteral : True | False;
NullLiteral : Null;

fragment

SChar : ~["\\\n\r] | '\\n' | '\\\\' | '\\"';

Identifier : [a-zA-Z] [a-zA-Z_0-9]*;

Whitespace : [ \t]+ -> skip ;
Newline : ('\r' '\n' ? | '\n') -> skip;
BlockComment : '/*' .*? '*/' -> skip;
LineComment : '//' ~[\r\n]* -> skip;