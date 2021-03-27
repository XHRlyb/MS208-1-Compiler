package Codegen;

import AST.declaration.classDef;
import AST.declaration.funDef;
import AST.declaration.typeNode;
import AST.expression.*;
import AST.statement.*;
import AST.*;
import Util.*;
import Util.symbol.Scope;
import Util.symbol.Type;
import Util.symbol.classType;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class IRbuilder implements ASTVisitor {
    public Scope glb, cur;
    public Type curRetTyp;
    public classType curCls;
    public boolean retDone;
    public int loopDep = 0;

    public IRbuilder(Scope glb) { this.glb = glb; }

    @Override
    public void visit(programNode o) {

    }

    @Override
    public void visit(blockStmt o) {

    }

    @Override
    public void visit(breakStmt o) {

    }

    @Override
    public void visit(continueStmt o) {

    }

    @Override
    public void visit(emptyStmt o) {

    }

    @Override
    public void visit(exprStmt o) {

    }

    @Override
    public void visit(forStmt o) {

    }

    @Override
    public void visit(ifStmt o) {

    }

    @Override
    public void visit(returnStmt o) {

    }

    @Override
    public void visit(varDefSigStmt o) {

    }

    @Override
    public void visit(varDefStmt o) {

    }

    @Override
    public void visit(whileStmt o) {

    }

    @Override
    public void visit(binaryExpr o) {

    }

    @Override
    public void visit(boolLiteral o) {

    }

    @Override
    public void visit(exprList o) {

    }

    @Override
    public void visit(funCallExpr o) {

    }

    @Override
    public void visit(intLiteral o) {

    }

    @Override
    public void visit(memberExpr o) {

    }

    @Override
    public void visit(newExpr o) {

    }

    @Override
    public void visit(nullLiteral o) {

    }

    @Override
    public void visit(prefixExpr o) {

    }

    @Override
    public void visit(stringLiteral o) {

    }

    @Override
    public void visit(subscriptExpr o) {

    }

    @Override
    public void visit(suffixExpr o) {

    }

    @Override
    public void visit(thisExpr o) {

    }

    @Override
    public void visit(varExpr o) {

    }

    @Override
    public void visit(classDef o) {

    }

    @Override
    public void visit(funDef o) {

    }

    @Override
    public void visit(typeNode o) {

    }
}