package Codegen;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Util.error.semanticError;
import Util.symbol.*;
import java.util.LinkedList;

public class toASM implements ASTVisitor {
    public Scope glb, cur;
    public Type curRetTyp;
    public classType curCls;
    public boolean retDone;
    public int loopDep = 0, scnt = 0;

    public int loop_id = 0, if_id = 0;
    public LinkedList<Integer> loop_idd = new LinkedList<Integer>();
    public LinkedList<Integer> if_idd = new LinkedList<Integer>();

    public int vid = 0, sid = 0;

    public toASM(Scope glb) { this.glb = glb; }

    @Override
    public void visit(programNode o) {
        //System.out.println("\t.text\n\t.align\t2\n\t.globl\tmain\n\t.typr\tmain,@function");
        //System.out.println("main:");
        funEntity main = glb.getFun("main", o.pos, false);
        o.body.forEach(x -> x.accept(this));
        //System.out.println("\t.size\tmain, .-main");
    }

    @Override
    public void visit(blockStmt o) {
        o.stmtLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(breakStmt o) {
        System.out.println("\tj\t.LOOP_END" + loop_idd.getFirst());
    }
    @Override
    public void visit(continueStmt o) {
        System.out.println("\tj\t.LOOP_BEG" + loop_idd.getFirst());
    }
    @Override
    public void visit(emptyStmt o) {}
    @Override
    public void visit(exprStmt o) {
        o.expr.accept(this);
        System.out.println("\tsw\ta0,"  + o.rid.id * (-4) +"(sp)");
    }
    @Override
    public void visit(forStmt o) {
        loopDep++; loop_idd.addFirst(++loop_id);
        if (o.ini != null) o.ini.accept(this);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        if (o.cond != null) o.cond.accept(this);
        System.out.println("\tbeq\ta0,zero,.LOOP_END" + loop_idd.getFirst());
        if (o.inc != null) o.inc.accept(this);
        cur = o.body.scp;
        o.body.accept(this);
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        cur = o.scp;
        loopDep--; loop_idd.removeFirst();
    }
    @Override
    public void visit(ifStmt o) {
        if_idd.addFirst(++if_id);
        o.cond.accept(this);
        System.out.println("\tbeq\ta0,zero,.IF_THEN_END" + if_idd.getFirst());
        cur = o.tStmt.scp;
        o.tStmt.accept(this);
        cur = cur.fa;
        if (o.fStmt != null) {
            System.out.println("\tj\t.IF_ELSE_END" + if_idd.getFirst());
            System.out.println(".IF_THEN_END" + if_idd.getFirst() + ":");
            cur = o.fStmt.scp;
            o.fStmt.accept(this);
            System.out.println(".IF_ELSE_END" + if_idd.getFirst() + ":");
        } else {
            System.out.println(".IF_THEN_END" + if_idd.getFirst() + ":");
        }
        if_idd.removeFirst();
    }
    @Override
    public void visit(returnStmt o) {
        retDone = true;
        cur = o.scp;
        if (o.retVal != null) {
            o.retVal.accept(this);
            System.out.println("\tj\t." + cur.abs_addr +"_END");
        }
    }
    @Override
    public void visit(varDefStmt o) {
        o.varLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(varDefSigStmt o) {
        cur = o.scp;
        int rid = cur.getVarRid(o.nam, o.pos, true).id;
        if (o.expr != null) {
            o.expr.accept(this);
            System.out.println("\tsw\ta0," + rid + "(sp)");
        }
    }
    @Override
    public void visit(whileStmt o) {
        loopDep++; loop_idd.addFirst(++loop_id);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        o.cond.accept(this);
        System.out.println("\tbeq\ta0,zero,.LOOP_END" + loop_idd.getFirst() + ":");
        o.body.accept(this);
        System.out.println("\tj\t.LOOP_BEG" + loop_idd.getFirst() + ":");
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        loopDep--; loop_idd.removeFirst();
    }

    @Override
    public void visit(binaryExpr o) {
        o.src2.accept(this);
        System.out.println("\tmv\ta1,a0");
        o.src1.accept(this);
        switch (o.op) {
            case "*":
                System.out.println("\tmul\ta0,a0,a1");
                break;
            case "/":
                System.out.println("\tdiv\ta0,a0,a1");
                break;
            case "%":
                System.out.println("\trem\ta0,a0,a1");
                break;
            case "-":
                System.out.println("\tsub\ta0,a0,a1");
                break;
            case "<<":
                System.out.println("\tsll\ta0,a0,a1");
                break;
            case ">>":
                System.out.println("\tsrl\ta0,a0,a1");
                break;
            case "&": case "&&":
                System.out.println("\tand\ta0,a0,a1");
                break;
            case "^":
                System.out.println("\txor\ta0,a0,a1");
                break;
            case "|": case "||":
                System.out.println("\tor\ta0,a0,a1");
                break;
            case "+":
                System.out.println("\tadd\ta0,a0,a1");
                break;
            case "<":
                System.out.println("\tslt\ta0,a0,a1");
                break;
            case ">":
                System.out.println("\tslt\ta0,a1,a0");
                break;
            case "<=":
                System.out.println("\tslt\ta0,a1,a0");
                System.out.println("\tnot\ta0,a0");
                break;
            case ">=":
                System.out.println("\tslt\ta0,a0,a1");
                System.out.println("\tnot\ta0,a0");
                break;
            case "==":
                System.out.println("\tsub\ta0,a0,a1");
                System.out.println("\tseqz\ta0,a0");
                break;
            case "!=":
                System.out.println("\tsub\ta0,a0,a1");
                System.out.println("\tsnez\ta0,a0");
                break;
            case "=":
                System.out.println("\tmv\ta1,a0");
                break;
        }
        System.out.println("\tsw\ta0,"  + o.rid.id * (-4) +"(sp)");
    }
    @Override
    public void visit(boolLiteral o) {
        if (o.val) System.out.println("\tli\ta0,1");
            else System.out.println("\tli\ta0,0");
        System.out.println("\tsw\ta0,"  + o.rid.id * (-4) +"(sp)");
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
        funEntity fun = (funEntity)o.bas.typ;
        System.out.println("\tli\ta0," + fun.params.size());
        o.params.forEach(x -> {
            x.accept(this);
            System.out.println("\tsw\ta0," + x.rid.id + "(sp)");
        });
        cur = o.scp;
        System.out.println("\tcall\t" + cur.abs_addr + fun.nam);
    }
    @Override
    public void visit(intLiteral o) {
        System.out.println("\tli\ta0," + o.val);
        System.out.println("\tsw\ta0,"  + o.rid.id * (-4) +"(sp)");
    }
    @Override
    public void visit(memberExpr o) {
        o.bas.accept(this);
        if (o.bas.typ instanceof arrayType && o.isFun && o.nam.equals("size")) {
            funEntity fun = new funEntity("size", "");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }         //???
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("length")) {
            funEntity fun = new funEntity("length", "");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("substring")) {
            funEntity fun = new funEntity("substring", "");
            fun.retTyp = new primitiveType("string");
            fun.params.add(new varEntity("left", new primitiveType("int")));
            fun.params.add(new varEntity("right", new primitiveType("int")));
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("parseInt")) {
            funEntity fun = new funEntity("parseInt", "");
            fun.retTyp = new primitiveType("int");
            o.typ = fun;
            return;
        }
        if (o.bas.typ.isString() && o.isFun && o.nam.equals("ord")) {
            funEntity fun = new funEntity("ord", "");
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
        o.scp = cur;
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
        o.scp = cur;
    }
    @Override
    public void visit(nullLiteral o) {}
    @Override
    public void visit(prefixExpr o) {      //GG
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
    }
    @Override
    public void visit(stringLiteral o) {
        System.out.println("\t.text\n\t.section\t.rodata\n\t.align\t2");
        System.out.println(".STRING" + (++scnt) + ":");
        System.out.println("\t.string\t" + o.val);
        System.out.println("\tlui\ta0,%hi(" + ".STRING" + (scnt) + ")");
        System.out.println("\taddi\ta0,a0,%lo(" + ".STRING" + (scnt) + ")");
        System.out.println("\tsw\ta0,"  + o.rid.id * (-4) +"(sp)");
    }
    @Override
    public void visit(subscriptExpr o) {
        o.bas.accept(this);
        o.offs.accept(this);
        System.out.println("\tmv\ta0,a1");
        System.out.println("\taddi\ta0,a0," + o.bas.rid);
        System.out.println("\tadd\ta0,a0,sp");
        System.out.println("\tlw\ta1,0(a0)");
        System.out.println("\tmv\ta1,a0");
    }
    @Override
    public void visit(suffixExpr o) {  //GG
        o.src.accept(this);
    }
    @Override
    public void visit(thisExpr o) {

    }
    @Override
    public void visit(varExpr o) {
        System.out.println("\tld\ta0,"  + o.rid.id * (-4) +"(sp)");
    }

    @Override
    public void visit(classDef o) { //???
        curCls = (classType)glb.typMap.get(o.nam);
        o.funLis.forEach(x -> {
            x.accept(this);
        });
        if (o.constructor != null) {
            o.constructor.accept(this);
        }
    }
    @Override
    public void visit(funDef o) { //???
        if (o.typ != null) curRetTyp = glb.getTyp(o.typ);
            else curRetTyp = new primitiveType("void");
        retDone = false;
        cur = o.scp;
        String curnam = o.abs_addr+o.nam;
        System.out.println("\t.text\n\t.align\t2\n\t.globl\t"+curnam+"\n\t.typr\t"+curnam+",@function");
        System.out.println(curnam+":");
        o.block.accept(this);
        System.out.println("\t.size\t"+curnam+", .-"+curnam);
        if (o.nam.equals("main")) retDone = true;
    }
    @Override
    public void visit(typeNode o) {
        o.scp = cur;
    }
}
