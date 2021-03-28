package Codegen;

import AST.*;
import AST.statement.*;
import AST.expression.*;
import AST.declaration.*;
import Util.error.semanticError;
import Util.symbol.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

public class toASM implements ASTVisitor {
    public Scope glb, cur;
    public Type curRetTyp;
    public classType curCls;
    public boolean retDone;
    public int loopDep = 0;
    public ArrayList<ASTNode> gVars = new ArrayList<>();

    public int loop_id = 0, if_id = 0, gggid = 0;
    public LinkedList<Integer> loop_idd = new LinkedList<Integer>();
    public LinkedList<Integer> if_idd = new LinkedList<Integer>();
    public HashMap<String, Scope> clss = new HashMap<>();
    public ArrayList<varDefSigStmt> gVarDefs = new ArrayList<>();

    public toASM(Scope glb) { this.glb = glb; }

    public void outGLB(varDefSigStmt x) {
        System.out.println("\t.globl\t.GLB" + ++gggid);
        System.out.println("\t.section\t.sbss,\"aw\",@nobits");
        System.out.println("\t.align\t2");
        System.out.println("\t.type\t.GLB" + gggid+",@object");
        if (clss.containsKey(x.nam)) {
            Scope tp = clss.get(x.nam);
            System.out.println("\t.size\t.GLB" + gggid+","+tp.allc.cnt * 4);
            System.out.println(".GLB"+gggid+":");
            System.out.println("\t.zero\t" + tp.allc.cnt * 4);
        } else {
            System.out.println("\t.size\t.GLB" + gggid+","+4);
            System.out.println(".GLB"+gggid+":");
            System.out.println("\t.zero\t" +4);
        }
    }
    public void initGLB(varDefSigStmt o, int id) {
        cur = glb;
        varEntity var = cur.getVar(o.nam, o.pos, false);
        var.vid.gid = id;
        if (o.expr != null) {
            o.expr.accept(this);
            System.out.println("\tlui\tt4,%hi(.GLB" + id+")");
            System.out.println("\tsw\tt3,%lo(.GLB"+id+")(t4)");
        }
    }
    @Override
    public void visit(programNode o) {
        cur = o.scp;
        for (int i = 0; i < o.body.size(); i++){
            ASTNode x = o.body.get(i);
            if (x instanceof varDefSigStmt || x instanceof  varDefStmt) {
                gVars.add(x);
                if (x instanceof  varDefSigStmt) gVarDefs.add((varDefSigStmt) x);
                else {
                    ((varDefStmt) x).varLis.forEach(y->gVarDefs.add((varDefSigStmt) y));
                }
            }
        };
        gVarDefs.forEach(x->outGLB(x));
        o.body.forEach(x -> {
            if (x instanceof varDefSigStmt || x instanceof  varDefStmt) {
            } else {
                x.accept(this);
            }
        });
    }

    @Override
    public void visit(blockStmt o) {
        cur = o.scp;
        o.stmtLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(breakStmt o) {
        cur = o.scp;
        System.out.println("\tj\t.LOOP_END" + loop_idd.getFirst());
    }
    @Override
    public void visit(continueStmt o) {
        cur = o.scp;
        System.out.println("\tj\t.LOOP_BEG" + loop_idd.getFirst());
    }
    @Override
    public void visit(emptyStmt o) {}
    @Override
    public void visit(exprStmt o) {
        cur = o.scp;
        o.expr.accept(this);
        if (o.rid.gid == 0) {
            System.out.println("\tsw\tt3,"  + o.rid.id * 4 +"(sp)");
        } else {
            System.out.println("\tlui\tt4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\tt3,%lo(.GLB"+o.rid.gid+")(t4)");
        }
    }
    @Override
    public void visit(forStmt o) {
        loopDep++; loop_idd.addFirst(++loop_id);
        cur = o.scp;
        if (o.ini != null) o.ini.accept(this);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        if (o.cond != null) o.cond.accept(this);
        System.out.println("\tbeq\tt3,zero,.LOOP_END" + loop_idd.getFirst());
        cur = o.body.scp;
        o.body.accept(this);
        if (o.inc != null) o.inc.accept(this);
        System.out.println("\tj\t.LOOP_BEG"+ loop_idd.getFirst());
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        cur = o.scp;
        loopDep--; loop_idd.removeFirst();
    }
    @Override
    public void visit(ifStmt o) {
        cur = o.scp;
        if_idd.addFirst(++if_id);
        o.cond.accept(this);
        System.out.println("\tbeq\tt3,zero,.IF_THEN_END" + if_idd.getFirst());
        cur = o.tStmt.scp;
        o.tStmt.accept(this);
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
        cur = o.scp;
        retDone = true;
        if (o.retVal != null) {
            o.retVal.accept(this);
            System.out.println("\tmv\ta0,t3");
            System.out.println("\tj\t." + cur.abs_addr +"_END");
        }
    }
    @Override
    public void visit(varDefStmt o) {
        cur = o.scp;
        o.varLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(varDefSigStmt o) {
        cur = o.scp;
        varEntity var = cur.getVar(o.nam, o.pos, false);
        if (o.expr != null) {
            o.expr.accept(this);
            System.out.println("\tsw\tt3," + var.vid.id * 4 + "(sp)");
        }
    }
    @Override
    public void visit(whileStmt o) {
        cur = o.scp;
        loopDep++; loop_idd.addFirst(++loop_id);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        o.cond.accept(this);
        System.out.println("\tbeq\tt3,zero,.LOOP_END" + loop_idd.getFirst() + ":");
        cur = o.body.scp;
        o.body.accept(this);
        System.out.println("\tj\t.LOOP_BEG" + loop_idd.getFirst() + ":");
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        loopDep--; loop_idd.removeFirst();
    }

    @Override
    public void visit(binaryExpr o) {
        cur = o.scp;
        o.src2.accept(this);
        System.out.println("\tmv\tt6,t3");
        o.src1.accept(this);
        System.out.println("\tmv\tt4,t6");
        switch (o.op) {
            case "*":
                System.out.println("\tmul\tt3,t3,t4");
                break;
            case "/":
                System.out.println("\tdiv\tt3,t3,t4");
                break;
            case "%":
                System.out.println("\trem\tt3,t3,t4");
                break;
            case "-":
                System.out.println("\tsub\tt3,t3,t4");
                break;
            case "<<":
                System.out.println("\tsll\tt3,t3,t4");
                break;
            case ">>":
                System.out.println("\tsrl\tt3,t3,t4");
                break;
            case "&": case "&&":
                System.out.println("\tand\tt3,t3,t4");
                break;
            case "^":
                System.out.println("\txor\tt3,t3,t4");
                break;
            case "|": case "||":
                System.out.println("\tor\tt3,t3,t4");
                break;
            case "+":
                System.out.println("\tadd\tt3,t3,t4");
                break;
            case "<":
                System.out.println("\tslt\tt3,t3,t4");
                break;
            case ">":
                System.out.println("\tslt\tt3,t4,t3");
                break;
            case "<=":
                System.out.println("\tslt\tt3,t4,t3");
                System.out.println("\txori\tt3,t3,1");
                break;
            case ">=":
                System.out.println("\tslt\tt3,t3,t4");
                System.out.println("\txori\tt3,t3,1");
                break;
            case "==":
                System.out.println("\tsub\tt3,t3,t4");
                System.out.println("\tseqz\tt3,t3");
                break;
            case "!=":
                System.out.println("\tsub\tt3,t3,t4");
                System.out.println("\tsnez\tt3,t3");
                break;
            case "=":
                System.out.println("\tmv\tt3,t4");
                if (o.src1.rid.gid == 0) {
                    System.out.println("\tsw\tt3,"+o.src1.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\tt4,%hi(.GLB"+o.src1.rid.gid+")");
                    System.out.println("\tsw\tt3,%lo(.GLB"+o.src1.rid.gid+")(t4)");
                }
                break;
        }
        if (o.rid.gid == 0) {
            System.out.println("\tsw\tt3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\tt4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\tt3,%lo(.GLB"+o.rid.gid+")(t4)");
        }
    }
    @Override
    public void visit(boolLiteral o) {
        cur = o.scp;
        if (o.val) System.out.println("\tli\tt3,1");
            else System.out.println("\tli\tt3,0");
        System.out.println("\tsw\tt3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(exprList o) {
        cur = o.scp;
        o.params.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(funCallExpr o) {
        cur = o.scp;
        if (o.bas instanceof varExpr) {  //???
            o.bas.typ = cur.getFun(((varExpr)o.bas).nam, o.pos, true);
        } else {
            cur = o.bas.scp;
            o.bas.accept(this);
        }
        funEntity fun = (funEntity)o.bas.typ;
        for (int i = 0; i < o.params.size(); i++) {
            exprNode x = o.params.get(i);
            cur = x.scp;
            x.accept(this);
            if (x.rid.gid == 0) {
                System.out.println("\tlw\ta"+String.valueOf(i)+"," +x.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\tt4,%hi(.GLB"+x.rid.gid+")");
                System.out.println("\tlw\ta"+String.valueOf(i)+",%lo(.GLB"+x.rid.gid+")(t4)");
            }
        }
        System.out.println("\tcall\t" + fun.abs_nam);
        //cur?????
    }
    @Override
    public void visit(intLiteral o) {
        cur = o.scp;
        System.out.println("\tli\tt3," + o.val);
        System.out.println("\tsw\tt3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(memberExpr o) {
        cur = o.bas.scp; // t3基地址，t4偏移量
        varEntity var = cur.getVar(o.nam, o.pos, true);
        if (o.bas.rid.gid == 0) {
            System.out.println("\tlw\tt3,"+o.bas.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\tt4,%hi(.GLB"+o.bas.rid.gid+")");
            System.out.println("\tlw\tt3,%lo(.GLB"+o.bas.rid.gid+")(t4)");
        }
        if (var.vid.gid == 0) {
            System.out.println("\tlw\tt3,"+var.vid.id * 4+"(t3)");
        } else {
            System.out.println("\taddi\tt4,t3,%hi(.GLB"+var.vid.gid+")");
            System.out.println("\tlw\tt3,%lo(.GLB"+var.vid.gid+")(t4)");
        }
        if (o.rid.gid == 0) {
            System.out.println("\tsw\tt3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\tt4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\tt3,%lo(.GLB"+o.rid.gid+")(t4)");
        }
        o.bas.accept(this);
    }/*
    public void newww(ArrayList<exprNode> x) {
        x.accept(this);
        System.out.println("\tmv\ta0,t3");
        System.out.println("\tcall\tmalloc");
        System.out.println("\tlw\ta0,"+x.rid.id+"(sp)");
    }*/
    @Override
    public void visit(newExpr o) {
        if (o.exprs != null) {
            //o.rid.id = newww(o.exprs.get(0));
            for (int i = 0; i < o.exprs.size(); i++) {
                exprNode x = o.exprs.get(i);
                x.accept(this);
            }
        }
        o.typ = glb.getTyp(o.typNd);  //???
        o.scp = cur;
    }
    @Override
    public void visit(nullLiteral o) {}
    @Override
    public void visit(prefixExpr o) {      //GG
        o.src.accept(this);     // t4是原值，t5是原值的地址，t3是新的值
        System.out.println("\tmv\tt4,t3");
        switch (o.op) {
            case "++":
                System.out.println("\taddi\tt4,t4,1");
                if (o.src.rid.gid == 0) {
                    System.out.println("\tsw\tt4,"+o.src.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\tt5,%hi(.GLB"+o.src.rid.gid+")");
                    System.out.println("\tsw\tt4,%lo(.GLB"+o.src.rid.gid+")(t5)");
                }
                System.out.println("\tmv\tt3,t4");
                break;
            case "--":
                System.out.println("\tsubi\tt4,t4,1");
                if (o.src.rid.gid == 0) {
                    System.out.println("\tsw\tt4,"+o.src.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\tt5,%hi(.GLB"+o.src.rid.gid+")");
                    System.out.println("\tsw\tt4,%lo(.GLB"+o.src.rid.gid+")(t5)");
                }
                System.out.println("\tmv\tt3,t4");
                break;
            case "+": break;
            case "-":
                System.out.println("\tsub\tt3,zero,t4");
                break;
            case "~":
                System.out.println("\tnot\tt3,t4");
                break;
            case "!":
                System.out.println("\txori\tt3,t4,1");
                break;
            default:
                break;
        }
        System.out.println("\tsw\tt3,"+o.rid.id+"(sp)");
    }
    @Override
    public void visit(stringLiteral o) {      //???????
        int scnt = o.id;
        System.out.println("\tlui\tt3,%hi(" + ".STRING" + (scnt) + ")");
        System.out.println("\taddi\tt3,t3,%lo(" + ".STRING" + (scnt) + ")");
        System.out.println("\tsw\tt3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(subscriptExpr o) {
        cur = o.scp;
        o.bas.accept(this);
        System.out.println("\tlw\tt3,"+o.bas.rid.id+"(sp)");
        System.out.println("\tmv\tt6,t3");
        o.offs.accept(this);
        System.out.println("\tadd\tt3,t3,t6");
        System.out.println("\tlw\tt3,0(t3)");
        System.out.println("\tsw\tt3,"+o.rid.id+"(sp)");
    }
    @Override
    public void visit(suffixExpr o) {  //GG
        o.src.accept(this);     // t3是原值也是新的值，t5是原值的地址
        switch (o.op) {
            case "++":
                System.out.println("\taddi\tt3,t3,1");
                if (o.src.rid.gid == 0) {
                    System.out.println("\tsw\tt3,"+o.src.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\tt5,%hi(.GLB"+o.src.rid.gid+")");
                    System.out.println("\tsw\tt3,%lo(.GLB"+o.src.rid.gid+")(t5)");
                }
                break;
            case "--":
                System.out.println("\tsubi\tt3,t3,1");
                if (o.src.rid.gid == 0) {
                    System.out.println("\tsw\tt3,"+o.src.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\tt5,%hi(.GLB"+o.src.rid.gid+")");
                    System.out.println("\tsw\tt3,%lo(.GLB"+o.src.rid.gid+")(t5)");
                }
                break;
            default:
                break;
        }
        System.out.println("\tsw\tt3,"+o.rid.id+"(sp)");
    }
    @Override
    public void visit(thisExpr o) {
        //??????
    }
    @Override
    public void visit(varExpr o) {
        if (o.rid.gid == 0) {
            System.out.println("\tlw\tt3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\tt4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\tt3,%lo(.GLB"+o.rid.gid+")(t4)");
        }
    }

    @Override
    public void visit(classDef o) { //???
        curCls = (classType)glb.typMap.get(o.nam);
        clss.put(o.nam, o.scp);
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
        cur = o.block.scp;
        String curnam = o.scp.abs_addr+o.nam;
        o.nam = curnam;
        System.out.println("\t.text\n\t.align\t2\n\t.globl\t"+curnam+"\n\t.type\t"+curnam+", @function");
        System.out.println(curnam+":");
        System.out.println("\taddi\tsp,sp,"+(cur.allc.cnt + 3) * (-4));
        System.out.println("\tsw\ts0,"+(cur.allc.cnt+2)*4+"(sp)");
        System.out.println("\tsw\tra,"+(cur.allc.cnt+1)*4+"(sp)");
        System.out.println("\taddi\ts0,sp,"+(cur.allc.cnt+3) * 4);
        if (o.nam.equals("main")) {
            for (int i = 0; i < gVarDefs.size(); i++) {
                varDefSigStmt x = gVarDefs.get(i);
                initGLB(x, i+1);
            }
        }
        o.block.accept(this);
        System.out.println("."+curnam+"_END:");
        System.out.println("\tlw\ts0,"+(cur.allc.cnt+2)*4+"(sp)");
        System.out.println("\tlw\tra,"+(cur.allc.cnt+1)*4+"(sp)");
        System.out.println("\taddi\tsp,sp,"+(cur.allc.cnt+3) * 4);
        System.out.println("\tret");
        System.out.println("\t.size\t"+curnam+", .-"+curnam);
        if (o.nam.equals("main")) retDone = true;
    }
    @Override
    public void visit(typeNode o) {
        o.scp = cur;
    }
}
