grammar Mx;

program : subprogram* EOF;
subprogram : varDef | funDef | classDef;

varDef : type varDefSig (',' varDefSig)* ';';
varDefSig : Identifier ('=' expression)?;
funDef : returnType? Identifier '(' paraLis? ')' block;
classDef : Class Identifier '{' (varDef | funDef)* '}' ';';

paraLis : para (',' para)*;
para : type Identifier;

type : basType ('[' ']')*;
basType : Int | Bool | String | Identifier;
returnType : type | Void;

block : '{' statement* '}';

primExp : '(' expression ')' | This | Identifier | literal;
literal : IntLiteral | BoolLiteral | StringLiteral | NullLiteral;

creator
    : basType ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+  #errorCreator
    | basType ('[' expression ']')+ ('[' ']')*                        #arrayCreator
    | basType '(' ')'                                                 #classCreator
    | basType                                                         #basicCreator
    ;

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

expression
    : primExp                                                         #atomExpr
    | expression '(' expressionLis? ')'                               #funCallExpr
    | bas=expression '[' offs=expression ']'                          #subscriptExpr
    | expression '.' Identifier                                       #memberExpr
    | <assoc=right> 'new' creator                                     #newExpr
    | expression op=('++' | '--')                                     #suffixExpr
    | <assoc=right> op=('++' | '--') expression                       #prefixExpr
    | <assoc=right> op=('+' | '-') expression                         #prefixExpr
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
    | <assoc=right> src1=expression op='=' src2=expression               #binaryExpr
    ;

expressionLis : expression (',' expression)*;

IntLiteral : [1-9] [0-9]* | '0';
StringLiteral : '"' (~["\\\n\r] | '\\' ["\\nr])* '"';
BoolLiteral : True | False;
NullLiteral : Null;

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

Identifier : [a-zA-Z] [a-zA-Z_0-9]*;

Whitespace : [ \t]+ -> skip;
Newline : ('\r' '\n'? | '\n') -> skip;
BlockComment : '/*' .*? '*/' -> skip;
LineComment : '//' ~[\r\n]* -> skip;