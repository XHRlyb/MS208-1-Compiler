package Frontend;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Util.symbol.Scope;
import Util.symbol.classType;
import Util.symbol.varEntity;

public class TypeCollector implements ASTVisitor {
    Scope glb;
    String curCls;

    public TypeCollector(Scope glb) { this.glb = glb; }

    @Override
    public void visit(programNode o) {
        curCls = null;
        o.body.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(blockStmt o) {}
    @Override
    public void visit(breakStmt o) {}
    @Override
    public void visit(continueStmt o) {}
    @Override
    public void visit(emptyStmt o) {}
    @Override
    public void visit(exprStmt o) {}
    @Override
    public void visit(forStmt o) {}
    @Override
    public void visit(ifStmt o) {}
    @Override
    public void visit(returnStmt o) {}
    @Override
    public void visit(varDefSigStmt o) {
        if (curCls == null) glb.varMap.get(o.nam).typ = glb.getTyp(o.typ);
        else ((classType)glb.typMap.get(curCls)).varMap.get(o.nam).typ = glb.getTyp(o.typ);
    }
    @Override
    public void visit(varDefStmt o) {}
    @Override
    public void visit(whileStmt o) {}

    @Override
    public void visit(binaryExpr o) {}
    @Override
    public void visit(boolLiteral o) {}
    @Override
    public void visit(exprList o) {}
    @Override
    public void visit(funCallExpr o) {}
    @Override
    public void visit(intLiteral o) {}
    @Override
    public void visit(memberExpr o) {}
    @Override
    public void visit(newExpr o) {}
    @Override
    public void visit(nullLiteral o) {}
    @Override
    public void visit(prefixExpr o) {}
    @Override
    public void visit(stringLiteral o) {}
    @Override
    public void visit(subscriptExpr o) {}
    @Override
    public void visit(suffixExpr o) {}
    @Override
    public void visit(thisExpr o) {}
    @Override
    public void visit(varExpr o) {}

    @Override
    public void visit(classDef o) {
        curCls = o.nam;
        o.varLis.forEach(x -> x.accept(this));
        o.funLis.forEach(x -> x.accept(this));
        if (o.constructor != null)
            ((classType)glb.typMap.get(curCls)).constructor.retTyp = null;
        curCls = null;
    }
    @Override
    public void visit(funDef o) {
        if (curCls == null) {
            glb.funMap.get(o.nam).retTyp = glb.getTyp(o.typ);
            o.params.forEach(x -> glb.funMap.get(o.nam).params.add(new varEntity(x.nam, glb.getTyp(x.typ))));
        } else {
            ((classType)glb.typMap.get(curCls)).funMap.get(o.nam).retTyp = glb.getTyp(o.typ);
            o.params.forEach(x -> ((classType)glb.typMap.get(curCls)).funMap.get(o.nam).params.add(new varEntity(x.nam, glb.getTyp(x.typ))));
        }
    }
    @Override
    public void visit(typeNode o) {}
}
