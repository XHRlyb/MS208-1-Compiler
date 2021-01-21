package AST;

import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;

public interface ASTVisitor {
    void visit(programNode o);

    void visit(blockStmt o);
    void visit(breakStmt o);
    void visit(continueStmt o);
    void visit(emptyStmt o);
    void visit(exprStmt o);
    void visit(forStmt o);
    void visit(ifStmt o);
    void visit(returnStmt o);
    void visit(varDefSigStmt o);
    void visit(varDefStmt o);
    void visit(whileStmt o);

    void visit(binaryExpr o);
    void visit(boolLiteral o);
    void visit(exprList o);
    void visit(funCallExpr o);
    void visit(intLiteral o);
    void visit(memberExpr o);
    void visit(newExpr o);
    void visit(nullLiteral o);
    void visit(prefixExpr o);
    void visit(stringLiteral o);
    void visit(subscriptExpr o);
    void visit(suffixExpr o);
    void visit(thisExpr o);
    void visit(varExpr o);

    void visit(classDef o);
    void visit(funDef o);
    void visit(typeNode o);
}