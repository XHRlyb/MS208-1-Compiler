package Frontend;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Util.error.semanticError;
import Util.symbol.*;

public class SemanticChecker implements ASTVisitor {
    public Scope glb, cur;
    public Type curRetTyp;
    public classType curCls;
    public boolean retDone;
    public int loopDep = 0;

    public SemanticChecker(Scope glb) { this.glb = glb; }

    @Override
    public void visit(programNode o) {
        funEntity main = glb.getFun("main", o.pos, false);
        if (!main.retTyp.isInt()) 
            throw new semanticError("main funtion must return int", o.pos);
        if (main.params.size() != 0)
            throw new semanticError("main funtion shouldn't have parameters", o.pos);
        cur = glb;
        o.body.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(blockStmt o) {
        o.stmtLis.forEach(x -> {
            if (x instanceof blockStmt) {
                cur = new Scope(cur);
                x.accept(this);
                cur = cur.fa;
            } else {
                x.accept(this);
            }
        });
    }
    @Override
    public void visit(breakStmt o) {
        if (loopDep == 0)
            throw new semanticError("break outside a loop", o.pos);
    }
    @Override
    public void visit(continueStmt o) {
        if (loopDep == 0)
            throw new semanticError("continue outside a loop", o.pos);
    }
    @Override
    public void visit(emptyStmt o) {}
    @Override
    public void visit(exprStmt o) {
        o.expr.accept(this);
    }
    @Override
    public void visit(forStmt o) {
        if (o.ini != null) o.ini.accept(this);
        if (o.cond != null) o.cond.accept(this);
        if (o.cond != null && !o.cond.typ.isBool())
            throw new semanticError("for cond must be bool", o.pos);
        if (o.inc != null) o.inc.accept(this);
        loopDep++;
        cur = new Scope(cur);
        o.body.accept(this);
        cur = cur.fa;
        loopDep--;
    }
    @Override
    public void visit(ifStmt o) {
        o.cond.accept(this);
        if (!o.cond.typ.isBool())
            throw new semanticError("if cond must be bool", o.pos);
        cur = new Scope(cur);
        o.tStmt.accept(this);
        cur = cur.fa;
        if (o.fStmt != null) {
            cur = new Scope(cur);
            o.fStmt.accept(this);
            cur = cur.fa;
        }
    }
    @Override
    public void visit(returnStmt o) {
        retDone = true;
        if (o.retVal != null) {
            o.retVal.accept(this);
            if (!o.retVal.typ.sameType(curRetTyp))
                throw new semanticError("return type error", o.pos);
        } else {
            if (!curRetTyp.isVoid())
                throw new semanticError("return type error", o.pos);
        }
    }
    @Override
    public void visit(varDefStmt o) {
        o.varLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(varDefSigStmt o) {
        Type varTyp = glb.getTyp(o.typ); //???
        if (varTyp.isVoid())
            throw new semanticError("void variable", o.pos);
        if (o.expr != null) {
            o.expr.accept(this);
            if (!o.expr.typ.sameType(varTyp))
                throw new semanticError("variable init fail", o.pos);
        }
        cur.defVar(o.nam, new varEntity(o.nam, varTyp), o.pos);
    }
    @Override
    public void visit(whileStmt o) {
        o.cond.accept(this);
        if (!o.cond.typ.isBool())
            throw new semanticError("while cond must be bool", o.pos);
        loopDep++;
        cur = new Scope(cur);
        o.body.accept(this);
        cur = cur.fa;
        loopDep--;
    }

    @Override
    public void visit(binaryExpr o) {
        o.src1.accept(this);
        o.src2.accept(this);
        switch (o.op) {
            case "*": case "/": case "%": case "-":
            case "<<": case ">>": case "&": case "^": case "|":
                if (!(o.src1.typ.isInt() && o.src2.typ.isInt()))
                    throw new semanticError("not int", o.pos);
                o.typ = new primitiveType("int");
                break;

            case "+":
                if (!( (o.src1.typ.isInt() && o.src2.typ.isInt())
                        || (o.src1.typ.isString() && o.src2.typ.isString()) ))
                    throw new semanticError("not int nor string", o.pos);
                o.typ = o.src1.typ;
                break;

            case "<": case ">": case "<=": case ">=":
                if (!( (o.src1.typ.isInt() && o.src2.typ.isInt())
                        || (o.src1.typ.isString() && o.src2.typ.isString()) ))
                    throw new semanticError("not int nor string", o.pos);
                o.typ = new primitiveType("bool");
                break;

            case "&&": case "||":
                if (!(o.src1.typ.isBool() && o.src2.typ.isBool()))
                    throw new semanticError("not bool", o.pos);
                o.typ = new primitiveType("bool");
                break;

            case "==": case "!=":
                if (!o.src1.typ.sameType(o.src2.typ))
                    throw new semanticError("not the same type", o.pos);
                o.typ = new primitiveType("bool");
                break;

            case "=":
                if (!o.src1.typ.sameType(o.src2.typ))
                    throw new semanticError("not the same type", o.pos);
                if (!o.src1.isAsg)
                    throw new semanticError("not assignable", o.pos);
                o.typ = o.src1.typ;
                o.isAsg = true;
                break;

            default:
                break;
        }
    }
    @Override
    public void visit(boolLiteral o) {
        o.typ = new primitiveType("bool");
    }
    @Override
    public void visit(exprList o) {}
    @Override
    public void visit(funCallExpr o) {
        if (o.bas instanceof varExpr) {  //???
            o.bas.typ = cur.getFun(((varExpr)o.bas).nam, o.pos, true);
        } else {
            o.bas.accept(this);
        }
        if (!(o.bas.typ instanceof funEntity))
            throw new semanticError("not a funtion", o.pos);
        funEntity fun = (funEntity)o.bas.typ;
        o.params.forEach(x -> x.accept(this));
        if (fun.params.size() != o.params.size())
            throw new semanticError("parameter size error", o.pos);
        for (int i = 0; i < fun.params.size(); i++) {
            if (!fun.params.get(i).typ.sameType(o.params.get(i).typ))
                throw new semanticError("parameter type error", o.pos);
        }
        o.typ = fun.retTyp;
    }
    @Override
    public void visit(intLiteral o) {
        o.typ = new primitiveType("int");
    }
    @Override
    public void visit(memberExpr o) {
        o.bas.accept(this);
        if (o.bas.typ instanceof arrayType && o.isFun && o.nam.equals("size")) {
            funEntity fun = new funEntity("size");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }         //???
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("length")) {
            funEntity fun = new funEntity("length");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("substring")) {
            funEntity fun = new funEntity("substring");
            fun.retTyp = new primitiveType("string");
            fun.params.add(new varEntity("left", new primitiveType("int")));
            fun.params.add(new varEntity("right", new primitiveType("int")));
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("parseInt")) {
            funEntity fun = new funEntity("parseInt");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("ord")) {
            funEntity fun = new funEntity("ord");
            fun.retTyp = new primitiveType("int");
            fun.params.add(new varEntity("pos", new primitiveType("int")));
            o.typ = fun;
            return;
        }
        if (!(o.bas.typ instanceof classType)) 
            throw new semanticError("no such class", o.pos);
        classType clsTyp = (classType)o.bas.typ;
        if (o.isFun) {
            if (clsTyp.funMap.containsKey(o.nam))
                o.typ = clsTyp.funMap.get(o.nam);
            else
                throw new semanticError("no such member " + o.nam, o.pos);
        } else {
            if (clsTyp.varMap.containsKey(o.nam))
                o.typ = clsTyp.varMap.get(o.nam).typ;
            else
                throw new semanticError("no such member " + o.nam, o.pos);
        }
    }
    @Override
    public void visit(newExpr o) {
        if (o.exprs != null) {
            o.exprs.forEach(x -> {
                x.accept(this);
                if (!x.typ.isInt())
                    throw new semanticError("not int", x.pos);
            });
        }
        o.typ = glb.getTyp(o.typNd);  //???
    }
    @Override
    public void visit(nullLiteral o) {
        o.typ = new primitiveType("null");
    }
    @Override
    public void visit(prefixExpr o) {
        o.src.accept(this);
        switch (o.op) {
            case "++": case "--":
                if (!o.src.isAsg)
                    throw new semanticError("not assignable", o.pos);
                o.isAsg = true;

            case "+": case "-": case "~":
                if (!o.src.typ.isInt())
                    throw new semanticError("not int", o.pos);
                break;

            case "!":  //???
                if (!o.src.typ.isBool())
                    throw new semanticError("not bool", o.pos);
                break;

            default:
                break;
        }
        o.typ = o.src.typ;
    }
    @Override
    public void visit(stringLiteral o) {
        o.typ = new primitiveType("string");
    }
    @Override
    public void visit(subscriptExpr o) {
        o.bas.accept(this);
        o.offs.accept(this);
        if (!(o.bas.typ instanceof arrayType))
            throw new semanticError("not an array", o.pos);
        if (!o.offs.typ.isInt())
            throw new semanticError("subscript must be int", o.pos);
        arrayType aryTyp = (arrayType)o.bas.typ;
        if (aryTyp.dim - 1 == 0) o.typ = aryTyp.typ;
        else o.typ = new arrayType(aryTyp.typ, aryTyp.dim - 1);
    }
    @Override
    public void visit(suffixExpr o) {
        o.src.accept(this);
        if (!o.src.typ.isInt())
            throw new semanticError("not int", o.pos);
        if (!o.src.isAsg)
            throw new semanticError("not assignable", o.pos);
        o.typ = o.src.typ;
    }
    @Override
    public void visit(thisExpr o) {
        if (curCls != null) o.typ = curCls;
        else throw new semanticError("this outside a class", o.pos);
    }
    @Override
    public void visit(varExpr o) {
        o.typ = cur.getVar(o.nam, o.pos, true).typ;
    }

    @Override
    public void visit(classDef o) { //???
        curCls = (classType)glb.typMap.get(o.nam);
        cur = new Scope(cur);
        curCls.varMap.forEach((key, value) -> cur.defVar(key, value, o.pos));
        curCls.funMap.forEach((key, value) -> cur.defFun(key, value, o.pos));
        o.funLis.forEach(x -> x.accept(this));
        if (o.constructor != null) {
            if (!o.constructor.nam.equals(o.nam))
                throw new semanticError("dismatched constructor name", o.pos);
            o.constructor.accept(this);
        }
        cur = cur.fa;
        curCls = null;
    }
    @Override
    public void visit(funDef o) { //???
        if (o.typ != null) curRetTyp = glb.getTyp(o.typ);
        else curRetTyp = new primitiveType("void");
        retDone = false;
        cur = new Scope(cur);
        o.params.forEach(x ->
                cur.defVar(x.nam, new varEntity(x.nam, glb.getTyp(x.typ)), x.pos)
        );
        o.block.accept(this);
        cur = cur.fa;
        if (o.nam.equals("main")) retDone = true;
        if (o.typ != null && !o.typ.typ.equals("void") && !retDone)
            throw new semanticError("No return", o.pos);
    }
    @Override
    public void visit(typeNode o) {}
}
