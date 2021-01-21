package Frontend;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Util.symbol.*;

public class SymbolCollector implements ASTVisitor {
    Scope glb, cur;

    public SymbolCollector(Scope glb) {
        this.glb = glb;
        this.glb.typMap.put("null", new primitiveType("null"));
        this.glb.typMap.put("void", new primitiveType("void"));
        this.glb.typMap.put("bool", new primitiveType("bool"));
        this.glb.typMap.put("int", new primitiveType("int"));
        this.glb.typMap.put("string", new primitiveType("string"));
        {
            funEntity fun = new funEntity("print");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("str", new primitiveType("string")));
            this.glb.funMap.put("print", fun);
        }
        {
            funEntity fun = new funEntity("println");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("str", new primitiveType("string")));
            this.glb.funMap.put("println", fun);
        }
        {
            funEntity fun = new funEntity("printInt");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("n", new primitiveType("int")));
            this.glb.funMap.put("printInt", fun);
        }
        {
            funEntity fun = new funEntity("printlnInt");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("n", new primitiveType("int")));
            this.glb.funMap.put("printlnInt", fun);
        }
        {
            funEntity fun = new funEntity("getString");
            fun.retTyp = new primitiveType("string");
            this.glb.funMap.put("getString", fun);
        }
        {
            funEntity fun = new funEntity("getInt");
            fun.retTyp = new primitiveType("int");
            this.glb.funMap.put("getInt", fun);
        }
        {
            funEntity fun = new funEntity("toString");
            fun.retTyp = new primitiveType("string");
            fun.params.add(new varEntity("i", new primitiveType("int")));
            this.glb.funMap.put("toString", fun);
        }
    }

    @Override
    public void visit(programNode o) {
        cur = glb;
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
        cur.defVar(o.nam, new varEntity(o.nam), o.pos);
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
        cur = new Scope(cur);
        classType a = new classType(o.nam);
        o.varLis.forEach(x -> x.accept(this));
        o.funLis.forEach(x -> x.accept(this));
        if (o.constructor != null)
            a.constructor = new funEntity(o.constructor.nam);
        a.varMap = cur.varMap;
        a.funMap = cur.funMap;
        cur = cur.fa;
        cur.defTyp(o.nam, a, o.pos);
    }
    @Override
    public void visit(funDef o) {
        cur.defFun(o.nam, new funEntity(o.nam), o.pos);
    }
    @Override
    public void visit(typeNode o) {}
}
