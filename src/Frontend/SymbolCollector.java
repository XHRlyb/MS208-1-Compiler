package Frontend;

import IR.*;
import IR.type.*;
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
        IR ir = new IR();
        {
            funEntity fun = new funEntity("print");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("str", new primitiveType("string")));
            this.glb.funMap.put("print", fun);
            fun.fun = new Func("print");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("println");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("str", new primitiveType("string")));
            this.glb.funMap.put("println", fun);
            fun.fun = new Func("println");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("printInt");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("n", new primitiveType("int")));
            this.glb.funMap.put("printInt", fun);
            fun.fun = new Func("printInt");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("printlnInt");
            fun.retTyp = new primitiveType("void");
            fun.params.add(new varEntity("n", new primitiveType("int")));
            this.glb.funMap.put("printlnInt", fun);
            fun.fun = new Func("printlnInt");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("getString");
            fun.retTyp = new primitiveType("string");
            this.glb.funMap.put("getString", fun);
            fun.fun = new Func("getString");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("getInt");
            fun.retTyp = new primitiveType("int");
            this.glb.funMap.put("getInt", fun);
            fun.fun = new Func("getInt");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
        }
        {
            funEntity fun = new funEntity("toString");
            fun.retTyp = new primitiveType("string");
            fun.params.add(new varEntity("i", new primitiveType("int")));
            this.glb.funMap.put("toString", fun);
            fun.fun = new Func("toString");
            fun.fun.rettyp = ir.getTyp(fun.retTyp);
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
        o.var = new varEntity(o.nam);
        cur.defVar(o.nam, o.var, o.pos);
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
        cur = new Scope(cur);//);
        classType a = new classType(o.nam);
        o.varLis.forEach(x -> x.accept(this));
        o.funLis.forEach(x -> x.accept(this));
        if (o.constructor != null) {
            a.constructor = new funEntity(o.constructor.nam);
            o.constructor.fun = a.constructor;
        }
        a.varMap = cur.varMap;
        a.funMap = cur.funMap;
        cur = cur.fa;
        cur.defTyp(o.nam, a, o.pos);
        o.clsTyp = a;
        o.clsTyp.clsTyp = new ClassType(o.nam);
    }
    @Override
    public void visit(funDef o) {
        o.fun = new funEntity(o.nam);
        cur.defFun(o.nam, o.fun, o.pos);
    }
    @Override
    public void visit(typeNode o) {}
}
