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

    public int loop_id = 0, if_id = 0, gggid = 0, jpid = 0;
    public LinkedList<Integer> loop_idd = new LinkedList<Integer>();
    public LinkedList<Integer> if_idd = new LinkedList<Integer>();
    public HashMap<String, Scope> clss = new HashMap<>();
    public HashMap<String, Scope> funs = new HashMap<>();
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
        cur = glb;
        varEntity var = cur.getVar(x.nam, x.pos, false);
        var.vid.gid = gggid;
    }
    public void initGLB(varDefSigStmt o, int id) {
        if (o.expr != null) {
            o.expr.accept(this);
            System.out.println("\tlui\ts4,%hi(.GLB" + id+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+id+")(s4)");
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
            System.out.println("\tsw\ts3,"  + o.rid.id * 4 +"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    @Override
    public void visit(forStmt o) {
        loopDep++; loop_idd.addFirst(++loop_id);
        cur = o.scp;
        if (o.ini != null) o.ini.accept(this);
        System.out.println(".LOOP_COND" + loop_idd.getFirst() + ":");
        if (o.cond != null) {
            o.cond.accept(this);
            System.out.println("\tbeq\ts3,zero,.LOOP_END" + loop_idd.getFirst());
        }
        cur = o.body.scp;
        o.body.accept(this);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        if (o.inc != null) o.inc.accept(this);
        System.out.println("\tj\t.LOOP_COND"+ loop_idd.getFirst());
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        cur = o.scp;
        loopDep--; loop_idd.removeFirst();
    }
    @Override
    public void visit(ifStmt o) {
        cur = o.scp;
        if_idd.addFirst(++if_id);
        o.cond.accept(this);
        System.out.println("\tbeq\ts3,zero,.IF_THEN_END" + if_idd.getFirst());
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
        retDone = true;
        if (o.retVal != null) {
            o.retVal.accept(this);
            System.out.println("\tmv\ta0,s3");
            cur = o.scp;
            System.out.println("\tj\t." + cur.abs_addr +"_END");
        } else {
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
        varEntity var = cur.getVar(o.nam, o.pos, true);
        if (o.expr != null) {
            o.expr.accept(this);
            System.out.println("\tsw\ts3," + var.vid.id * 4 + "(sp)");
        }
    }
    @Override
    public void visit(whileStmt o) {
        cur = o.scp;
        loopDep++; loop_idd.addFirst(++loop_id);
        System.out.println(".LOOP_BEG" + loop_idd.getFirst() + ":");
        o.cond.accept(this);
        System.out.println("\tbeq\ts3,zero,.LOOP_END" + loop_idd.getFirst());
        cur = o.body.scp;
        o.body.accept(this);
        System.out.println("\tj\t.LOOP_BEG" + loop_idd.getFirst());
        System.out.println(".LOOP_END" + loop_idd.getFirst() + ":");
        loopDep--; loop_idd.removeFirst();
    }

    @Override
    public void visit(binaryExpr o) {
        cur = o.scp;

        if (o.op.equals("&&")) {
            int jid = ++jpid;
            o.src1.accept(this);
            System.out.println("\tbeqz\ts3,.SF"+jid);
            o.src2.accept(this);
            System.out.println("\tbeqz\ts3,.SF"+jid);
            System.out.println("\tli\ts3,1");
            System.out.println("\tj\t.SE"+jid);
            System.out.println(".SF"+jid+":");
            System.out.println("\tli\ts3,0");
            System.out.println(".SE"+jid+":");
            if (o.rid.gid == 0) {
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
            }
            return;
        } else if (o.op.equals("||")) {
            int jid = ++jpid;
            o.src1.accept(this);
            System.out.println("\tbnez\ts3,.ST"+jid);
            o.src2.accept(this);
            System.out.println("\tbnez\ts3,.ST"+jid);
            System.out.println("\tli\ts3,0");
            System.out.println("\tj\t.SE"+jid);
            System.out.println(".ST"+jid+":");
            System.out.println("\tli\ts3,1");
            System.out.println(".SE"+jid+":");
            if (o.rid.gid == 0) {
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
            }
            return;
        }

        o.src2.accept(this);
        System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        /*if (o.src2.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.src2.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.src2.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.src2.rid.gid+")(s4)");
        }*/

        if (o.op.equals("=")) {
            if (o.src1 instanceof subscriptExpr) {
                getsubs((subscriptExpr)o.src1);
                /*if (o.src2.rid.gid == 0) {
                    System.out.println("\tlw\ts4," + o.src2.rid.id * 4 + "(sp)");
                } else {
                    System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
                    System.out.println("\tlw\ts4,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
                }*/
                System.out.println("\tlw\ts4," + o.rid.id * 4 + "(sp)");
                System.out.println("\tsw\ts4,0(s3)");
            } else if (o.src1 instanceof memberExpr) {
                getmem((memberExpr)o.src1);
                /*if (o.src2.rid.gid == 0) {
                    System.out.println("\tlw\ts4," + o.src2.rid.id * 4 + "(sp)");
                } else {
                    System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
                    System.out.println("\tlw\ts4,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
                }*/
                System.out.println("\tlw\ts4," + o.rid.id * 4 + "(sp)");
                System.out.println("\tsw\ts4,0(s3)");
            } else if (o.src1 instanceof varExpr) {
                getvar((varExpr)o.src1);
                /*if (o.src2.rid.gid == 0) {
                    System.out.println("\tlw\ts4," + o.src2.rid.id * 4 + "(sp)");
                } else {
                    System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
                    System.out.println("\tlw\ts4,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
                }*/
                System.out.println("\tlw\ts4," + o.rid.id * 4 + "(sp)");
                System.out.println("\tsw\ts4,0(s3)");
                System.out.println("\tmv\ts3,s4");
            } else {
                o.src1.accept(this);
                /*if (o.src2.rid.gid == 0) {
                    System.out.println("\tlw\ts4," + o.src2.rid.id * 4 + "(sp)");
                } else {
                    System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
                    System.out.println("\tlw\ts4,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
                }*/
                System.out.println("\tlw\ts4," + o.rid.id * 4 + "(sp)");
                if (o.src1.rid.gid == 0) {
                    System.out.println("\tsw\ts4,"+o.src1.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\ts5,%hi(.GLB"+o.src1.rid.gid+")");
                    System.out.println("\tsw\ts4,%lo(.GLB"+o.src1.rid.gid+")(s5)");
                }
            }
            /*if (o.rid.gid == 0) {
                System.out.println("\tsw\ts4,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts5,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ts4,%lo(.GLB"+o.rid.gid+")(s5)");
            }*/
            System.out.println("\tmv\ts3,s4");
            return;
        }

        o.src1.accept(this);

        if (o.src1.typ.isString() && o.src2.typ.isString()) {
            System.out.println("\tmv\ta0,s3");
            /*if (o.src2.rid.gid == 0) {
                System.out.println("\tlw\ta1," + o.src2.rid.id * 4 + "(sp)");
            } else {
                System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
                System.out.println("\tlw\ta1,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
            }*/
            System.out.println("\tlw\ta1," + o.rid.id * 4 + "(sp)");
            switch (o.op) {
                case "+":
                    System.out.println("\tcall\tmy_string_plus");
                    break;
                case "<":
                    System.out.println("\tcall\tmy_string_le");
                    break;
                case ">":
                    System.out.println("\tcall\tmy_string_ge");
                    break;
                case "<=":
                    System.out.println("\tcall\tmy_string_leq");
                    break;
                case ">=":
                    System.out.println("\tcall\tmy_string_geq");
                    break;
                case "==":
                    System.out.println("\tcall\tmy_string_eq");
                    break;
                case "!=":
                    System.out.println("\tcall\tmy_string_neq");
                    break;
            }
            if (o.rid.gid == 0) {
                System.out.println("\tsw\ta0,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ta0,%lo(.GLB"+o.rid.gid+")(s4)");
            }
            System.out.println("\tmv\ts3,a0");
            return;
        }

        /*if (o.src2.rid.gid == 0) {
            System.out.println("\tlw\ts4," + o.src2.rid.id * 4 + "(sp)");
        } else {
            System.out.println("\tlui\ts5,%hi(.GLB" + o.src2.rid.gid + ")");
            System.out.println("\tlw\ts4,%lo(.GLB" + o.src2.rid.gid + ")(s5)");
        }*/
        System.out.println("\tlw\ts4," + o.rid.id * 4 + "(sp)");
        switch (o.op) {
            case "*":
                System.out.println("\tmul\ts3,s3,s4");
                break;
            case "/":
                System.out.println("\tdiv\ts3,s3,s4");
                break;
            case "%":
                System.out.println("\trem\ts3,s3,s4");
                break;
            case "-":
                System.out.println("\tsub\ts3,s3,s4");
                break;
            case "<<":
                System.out.println("\tsll\ts3,s3,s4");
                break;
            case ">>":
                System.out.println("\tsrl\ts3,s3,s4");
                break;
            case "&":
            case "&&":
                System.out.println("\tand\ts3,s3,s4");
                break;
            case "^":
                System.out.println("\txor\ts3,s3,s4");
                break;
            case "|":
            case "||":
                System.out.println("\tor\ts3,s3,s4");
                break;
            case "+":
                System.out.println("\tadd\ts3,s3,s4");
                break;
            case "<":
                System.out.println("\tslt\ts3,s3,s4");
                break;
            case ">":
                System.out.println("\tslt\ts3,s4,s3");
                break;
            case "<=":
                System.out.println("\tslt\ts3,s4,s3");
                System.out.println("\txori\ts3,s3,1");
                break;
            case ">=":
                System.out.println("\tslt\ts3,s3,s4");
                System.out.println("\txori\ts3,s3,1");
                break;
            case "==":
                System.out.println("\tsub\ts3,s3,s4");
                System.out.println("\tseqz\ts3,s3");
                break;
            case "!=":
                System.out.println("\tsub\ts3,s3,s4");
                System.out.println("\tsnez\ts3,s3");
                break;
        }
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    @Override
    public void visit(boolLiteral o) {
        cur = o.scp;
        if (o.val) System.out.println("\tli\ts3,1");
            else System.out.println("\tli\ts3,0");
        System.out.println("\tsw\ts3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(exprList o) {
        cur = o.scp;
        o.params.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(funCallExpr o) {
        cur = o.scp;
        if (!(o.bas instanceof varExpr)) {
            o.bas.accept(this);
            System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
        }
        funEntity fun = (funEntity)o.bas.typ;
        if (o.bas instanceof memberExpr && ((memberExpr)o.bas).bas.typ instanceof arrayType) {
            System.out.println("\tlw\ts3,0(s3)");
            System.out.println("\tmv\ta0,s3");
            System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
            return;
        }
        if (o.bas instanceof memberExpr && ((memberExpr)o.bas).bas.typ.isString()) {
            System.out.println("\tmv\ta0,s3");
            for (int i = 0; i < o.params.size(); i++) {
                exprNode x = o.params.get(i);
                cur = x.scp;
                x.accept(this);
                if (x.rid.id != 0 && i <= 6) {
                    if (x.rid.gid == 0) {
                        //System.out.println("\tlw\ta" + String.valueOf(i+1) + "," + x.rid.id * 4 + "(sp)");
                        System.out.println("\tmv\ta" + String.valueOf(i+1) + ",s3");
                    } else {
                        System.out.println("\tlui\ts4,%hi(.GLB" + x.rid.gid + ")");
                        System.out.println("\tlw\ta" + String.valueOf(i+1) + ",%lo(.GLB" + x.rid.gid + ")(s4)");
                    }
                }
            }
            if (fun.nam.equals("length")) {
                System.out.println("\tcall\tmy_c_string_length");
            } else if (fun.nam.equals("substring")) {
                System.out.println("\tcall\tmy_c_string_substring");
            } else if (fun.nam.equals("parseInt")) {
                System.out.println("\tcall\tmy_c_string_parseInt");
            } else if (fun.nam.equals("ord")) {
                System.out.println("\tcall\tmy_c_string_ord");
            }
            System.out.println("\tmv\ts3,a0");
            System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
            return;
        }
        //if (curCls != null) {
            System.out.println("\tsw\ts7,"+(o.rid.id+1)*4+"(sp)");
        //}
        for (int i = 0; i < o.params.size(); i++) {
            exprNode x = o.params.get(i);
            //cur = x.scp;
            x.accept(this);
            if (x.rid.id != 0 && i <= 7) {
                System.out.println("\tmv\ta" + String.valueOf(i) + ",s3");
                /*if (x.rid.gid == 0) {
                //System.out.println("\tlw\ta" + String.valueOf(i) + "," + x.rid.id * 4 + "(sp)");
                System.out.println("\tmv\ta" + String.valueOf(i) + ",s3");
                } else {
                    System.out.println("\tlui\ts4,%hi(.GLB" + x.rid.gid + ")");
                    System.out.println("\tlw\ta" + String.valueOf(i) + ",%lo(.GLB" + x.rid.gid + ")(s4)");
                }*/
            }
        }
        if (!(o.bas instanceof varExpr)) {
            System.out.println("\tlw\ts7,"+o.rid.id*4+"(sp)");
        }
        System.out.println("\tcall\t" + fun.abs_nam);
        System.out.println("\tmv\ts3,a0");
        //if (curCls != null) {
            System.out.println("\tlw\ts7,"+(o.rid.id+1)*4+"(sp)");
        //}
        System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
    }
    @Override
    public void visit(intLiteral o) {
        cur = o.scp;
        System.out.println("\tli\ts3," + o.val);
        System.out.println("\tsw\ts3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(memberExpr o) {
        cur = clss.get(o.nam); // s3基地址，s4偏移量
        //System.out.println(o.nam);
        o.bas.accept(this);
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        if (o.bas.typ instanceof arrayType && o.isFun && o.nam.equals("size")) {
            return;
        }         //???
        if (o.bas.typ.isString() && o.isFun) {
            return;
        }
        classType clsTyp = (classType)o.bas.typ;
        if (o.isFun) {
            //
        } else {
            cur = clss.get(clsTyp.nam);
            varEntity var = cur.getVar(o.nam, o.pos, true);
            if (var.vid.gid == 0) {
                System.out.println("\tlw\ts3,"+var.vid.id * 4+"(s3)");
            } else {
                System.out.println("\taddi\ts3,s3,%hi(.GLB"+var.vid.gid+")");
                System.out.println("\tlw\ts3,%lo(.GLB"+var.vid.gid+")(s5)");
            }
            if (o.rid.gid == 0) {
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
            }
        }
    }
    public void getmem(memberExpr o) {
        o.bas.accept(this);
        classType clsTyp = (classType)o.bas.typ;
        cur = clss.get(clsTyp.nam);
        varEntity var = cur.getVar(o.nam, o.pos, true);
        if (var.vid.gid == 0) {
            System.out.println("\taddi\ts3,s3,"+var.vid.id * 4);
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+var.vid.gid+")");
            System.out.println("\taddi\ts4,s4,%hi(.GLB"+var.vid.gid+")");
            System.out.println("\tslli\ts4,s4,2");
            System.out.println("\tadd\ts3,s3,s4");
        }
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    public void newww(newExpr o, int id) {
        if (id == o.exprs.size()) return;
        exprNode x = o.exprs.get(id);
        int lid = ++loop_id, cnt = o.exprs.size();
        if (id == o.exprs.size() - 1) {
            if (x.rid.gid == 0) {
                System.out.println("\tlw\ts4,"+x.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts5,%hi(.GLB"+x.rid.gid+")");
                System.out.println("\tlw\ts4,%lo(.GLB"+x.rid.gid+")(s5)");
            }
            cur = clss.get(o.typNd.typ);
            if (cur != null)
                System.out.println("\tli\ts5,"+cur.allc.cnt*4);
            else
                System.out.println("\tli\ts5,4");
            System.out.println("\taddi\ts3,s4,1");
            System.out.println("\tmul\ts3,s3,s5");
            System.out.println("\tmv\ta0,s3");
            System.out.println(("\tcall\tmalloc"));
            System.out.println(("\tsw\ta0,"+(o.rid.id+id+1)*4+"(sp)"));
            if (id == 0) {
                if (o.rid.gid == 0) {
                    System.out.println("\tsw\ta0,"+o.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                    System.out.println("\tsw\ta0,%lo(.GLB"+o.rid.gid+")(s4)");
                }
            }
            System.out.println("\tsw\ts4,0(a0)");
            return;
        }
        if (x.rid.gid == 0) {
            System.out.println("\tlw\ts4,"+x.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts5,%hi(.GLB"+x.rid.gid+")");
            System.out.println("\tlw\ts4,%lo(.GLB"+x.rid.gid+")(s5)");
        }
        System.out.println("\taddi\ts3,s4,1");
        System.out.println("\tslli\ts3,s3,2");
        System.out.println("\tmv\ta0,s3");
        System.out.println(("\tcall\tmalloc"));
        System.out.println(("\tsw\ta0,"+(o.rid.id+id+1)*4+"(sp)"));
        if (id == 0) {
            if (o.rid.gid == 0) {
                System.out.println("\tsw\ta0,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tsw\ta0,%lo(.GLB"+o.rid.gid+")(s4)");
            }
        }
        else {
            System.out.println(("\tlw\ts3,"+(o.rid.id+id)*4+"(sp)"));
            System.out.println(("\taddi\ts3,s3,4"));
            System.out.println(("\tsw\ta0,0(sp)"));
        }
        System.out.println("\tsw\ts4,0(a0)");

        System.out.println("\tli\ts3,1");
        System.out.println(("\tsw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
        System.out.println(".LOOP_BEG"+lid+":");
        System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
        if (x.rid.gid == 0) {
            System.out.println("\tlw\ts4,"+x.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts5,%hi(.GLB"+x.rid.gid+")");
            System.out.println("\tlw\ts4,%lo(.GLB"+x.rid.gid+")(s5)");
        }
        System.out.println("\tblt\ts4,s3,.LOOP_END"+lid);
        newww(o, id+1);
        System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
        System.out.println("\taddi\ts3,s3,1");
        System.out.println(("\tsw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
        System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
        System.out.println("\tj\t.LOOP_BEG"+lid);
        System.out.println(".LOOP_END"+lid+":");
    }

/*public void newww(newExpr o, int id) {
    if (id == o.exprs.size()) return;
    exprNode x = o.exprs.get(id);
    int lid = ++loop_id, cnt = o.exprs.size();
    if (x.rid.gid == 0) {
        System.out.println("\tlw\ts4,"+x.rid.id * 4+"(sp)");
    } else {
        System.out.println("\tlui\ts5,%hi(.GLB"+x.rid.gid+")");
        System.out.println("\tlw\ts4,%lo(.GLB"+x.rid.gid+")(s5)");
    }
    System.out.println("\taddi\ts3,s4,1");
    System.out.println("\tmul\ts3,s3,s6");
    System.out.println("\tmv\ta0,s3");
    System.out.println(("\tcall\tmalloc"));
    System.out.println(("\tsw\ta0,"+(o.rid.id+id+1)*4+"(sp)"));
    if (id == 0) {
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ta0,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ta0,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    else {
        System.out.println(("\tlw\ts3,"+(o.rid.id+id)*4+"(sp)"));
        System.out.println(("\taddi\ts3,s3,4"));
        System.out.println(("\tsw\ta0,0(sp)"));
    }
    System.out.println("\tsw\ts4,0(a0)");

    System.out.println("\tli\ts3,1");
    System.out.println(("\tsw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
    System.out.println(".LOOP_BEG"+lid+":");
    System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
    if (x.rid.gid == 0) {
        System.out.println("\tlw\ts4,"+x.rid.id * 4+"(sp)");
    } else {
        System.out.println("\tlui\ts5,%hi(.GLB"+x.rid.gid+")");
        System.out.println("\tlw\ts4,%lo(.GLB"+x.rid.gid+")(s5)");
    }
    System.out.println("\tblt\ts4,s3,.LOOP_END"+lid);
    newww(o, id+1);
    System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
    System.out.println("\taddi\ts3,s3,1");
    System.out.println(("\tsw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
    System.out.println(("\tlw\ts3,"+(o.rid.id+id+cnt+1)*4+"(sp)"));
    System.out.println("\tj\t.LOOP_BEG"+lid);
    System.out.println(".LOOP_END"+lid+":");
}*/
    @Override
    public void visit(newExpr o) {
        if (o.exprs != null) {
            //o.rid.id = newww(o.exprs.get(0));
            for (int i = 0; i < o.exprs.size(); i++) {
                exprNode x = o.exprs.get(i);
                cur = x.scp;
                x.accept(this);
                // to modify
                if (x.rid.gid == 0) {
                    System.out.println("\tsw\ts3,"+x.rid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\ts4,%hi(.GLB"+x.rid.gid+")");
                    System.out.println("\tsw\ts3,%lo(.GLB"+x.rid.gid+")(s4)");
                }
            }
            cur = o.scp;
            newww(o,0);
            if (o.rid.gid == 0) {
                System.out.println("\tlw\ts3,"+o.rid.id * 4+"(sp)");
            } else {
                System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
                System.out.println("\tlw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
            }
        } else {
            cur = clss.get(o.typNd.typ);
            System.out.println("\tli\ta0,"+(cur.allc.cnt + 1) * 4);
            System.out.println("\tcall\tmalloc");
            System.out.println("\tli\ts3,1");
            System.out.println("\tsw\ts3,0(a0)");
            System.out.println("\tmv\ts3,a0");
        }
        /*if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }*/
    }
    @Override
    public void visit(nullLiteral o) {
        System.out.println("\tmv\ts3,zero");
    }
    @Override
    public void visit(prefixExpr o) {      //GG
        o.src.accept(this);     // s4是原值，s5是原值的地址，s3是新的值
        System.out.println("\tmv\ts4,s3");
        switch (o.op) {
            case "++":
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
                if (o.src instanceof subscriptExpr) {
                    getsubs((subscriptExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else if (o.src instanceof memberExpr) {
                    getmem((memberExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else if (o.src instanceof varExpr) {
                    getvar((varExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else {
                    System.out.println("\taddi\ts3,s3,1");
                    if (o.src.rid.gid == 0) {
                        System.out.println("\tsw\ts3,"+o.src.rid.id * 4+"(sp)");
                    } else {
                        System.out.println("\tlui\ts5,%hi(.GLB"+o.src.rid.gid+")");
                        System.out.println("\tsw\ts3,%lo(.GLB"+o.src.rid.gid+")(s5)");
                    }
                }
                break;
            case "--":
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
                if (o.src instanceof subscriptExpr) {
                    getsubs((subscriptExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else if (o.src instanceof memberExpr) {
                    getmem((memberExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else if (o.src instanceof varExpr) {
                    getvar((varExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s5");
                } else {
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tsub\ts3,s3,s6");
                    if (o.src.rid.gid == 0) {
                        System.out.println("\tsw\ts3,"+o.src.rid.id * 4+"(sp)");
                    } else {
                        System.out.println("\tlui\ts5,%hi(.GLB"+o.src.rid.gid+")");
                        System.out.println("\tsw\ts3,%lo(.GLB"+o.src.rid.gid+")(s5)");
                    }
                }
                break;
            case "+": break;
            case "-":
                System.out.println("\tsub\ts3,zero,s4");
                break;
            case "~":
                System.out.println("\tnot\ts3,s4");
                break;
            case "!":
                System.out.println("\txori\ts3,s4,1");
                break;
            default:
                break;
        }
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    @Override
    public void visit(stringLiteral o) {      //???????
        int scnt = o.id;
        System.out.println("\tlui\ts3,%hi(" + ".STRING" + (scnt) + ")");
        System.out.println("\taddi\ts3,s3,%lo(" + ".STRING" + (scnt) + ")");
        System.out.println("\tsw\ts3,"  + o.rid.id * 4 +"(sp)");
    }
    @Override
    public void visit(subscriptExpr o) {
        cur = o.scp;
        o.bas.accept(this);
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        o.offs.accept(this);
        System.out.println("\tmv\ts6,s3");
        /*
        if (o.offs.rid.gid == 0) {
            System.out.println("\tlw\ts6,"+(o.offs.rid.id)*4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+(o.offs.rid.gid+")");
            System.out.println("\tlw\ts6,%lo(.GLB"+o.offs.rid.gid+")(s4)");
        }*/
        System.out.println("\taddi\ts6,s6,1");
        System.out.println("\tslli\ts6,s6,2");
        if (o.rid.gid == 0) {
            System.out.println("\tlw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        System.out.println("\tadd\ts3,s3,s6");
        System.out.println("\tlw\ts3,0(s3)");
        System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
    }
    public void getsubs(subscriptExpr o) {
        cur = o.scp;
        o.bas.accept(this);
        if (o.rid.gid == 0) {
            System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tsw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        o.offs.accept(this);
        System.out.println("\tmv\ts6,s3");
        System.out.println("\taddi\ts6,s6,1");
        System.out.println("\tslli\ts6,s6,2");
        if (o.rid.gid == 0) {
            System.out.println("\tlw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        System.out.println("\tadd\ts3,s3,s6");
        /*if (o.offs.rid.gid == 0) {
            System.out.println("\tlw\ts6,"+o.offs.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.offs.rid.gid+")");
            System.out.println("\tlw\ts6,%lo(.GLB"+o.offs.rid.gid+")(s4)");
        }
        System.out.println("\taddi\ts6,s6,1");
        System.out.println("\tli\ts4,4");
        System.out.println("\tmul\ts6,s4,s6");
        if (o.rid.gid == 0) {
            System.out.println("\tlw\ts3,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
        System.out.println("\tadd\ts3,s3,s6");*/
        System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
    }
    @Override
    public void visit(suffixExpr o) {  //GG
        o.src.accept(this);     // s3是原值也是新的值，s5是原值的地址
        switch (o.op) {
            case "++":
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
                if (o.src instanceof subscriptExpr) {
                    getsubs((subscriptExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else if (o.src instanceof memberExpr) {
                    getmem((memberExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else if (o.src instanceof varExpr) {
                    getvar((varExpr)o.src);
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\taddi\ts5,s4,1");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else {
                    System.out.println("\taddi\ts4,s3,1");
                    if (o.src.rid.gid == 0) {
                        System.out.println("\tsw\ts4,"+o.src.rid.id * 4+"(sp)");
                    } else {
                        System.out.println("\tlui\ts5,%hi(.GLB"+o.src.rid.gid+")");
                        System.out.println("\tsw\ts4,%lo(.GLB"+o.src.rid.gid+")(s5)");
                    }
                }
                break;
            case "--":
                System.out.println("\tsw\ts3,"+o.rid.id * 4+"(sp)");
                if (o.src instanceof subscriptExpr) {
                    getsubs((subscriptExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else if (o.src instanceof memberExpr) {
                    getmem((memberExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else if (o.src instanceof varExpr) {
                    getvar((varExpr)o.src);
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tlw\ts4,"+o.rid.id * 4+"(sp)");
                    System.out.println("\tsub\ts5,s4,s6");
                    System.out.println("\tsw\ts5,0(s3)");
                    System.out.println("\tmv\ts3,s4");
                } else {
                    System.out.println("\tli\ts6,1");
                    System.out.println("\tsub\ts4,s3,s6");
                    if (o.src.rid.gid == 0) {
                        System.out.println("\tsw\ts4,"+o.src.rid.id * 4+"(sp)");
                    } else {
                        System.out.println("\tlui\ts5,%hi(.GLB"+o.src.rid.gid+")");
                        System.out.println("\tsw\ts4,%lo(.GLB"+o.src.rid.gid+")(s5)");
                    }
                }
                break;
            default:
                break;
        }
        System.out.println("\tsw\ts3,"+o.rid.id*4+"(sp)");
    }
    @Override
    public void visit(thisExpr o) {
        //??????
        System.out.println("\tmv\ts3,s7");
        /*if (o.rid.gid == 0) {
            System.out.println("\tlw\ts7,"+o.rid.id * 4+"(sp)");
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\ts7,%lo(.GLB"+o.rid.gid+")(s4)");
        }*/
    }
    @Override
    public void visit(varExpr o) {
        cur = o.scp;
        varEntity var = cur.getVar(o.nam, o.pos, true);
        //System.out.println(var.vid.id);
        //System.out.println((cur == glb)+var.nam+cur.contVar(o.nam, false));
        /*if (var.vid.gid == 0) {
            if (var.incls) {
                System.out.println("\tlw\ts3,"+var.vid.id * 4+"(s7)");
            } else {
                System.out.println("\tlw\ts3,"+var.vid.id * 4+"(sp)");
            }
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+var.vid.gid+")");
            System.out.println("\tlw\ts3,%lo(.GLB"+var.vid.gid+")(s4)");
        }*/
        if (o.rid.gid == 0) {
            if (o.inCls) {
                System.out.println("\tlw\ts3,"+o.rid.id * 4+"(s7)");
            } else {
                System.out.println("\tlw\ts3,"+o.rid.id * 4+"(sp)");
            }
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\tlw\ts3,%lo(.GLB"+o.rid.gid+")(s4)");
        }
    }
    public void getvar(varExpr o) {
        cur = o.scp;
        if (o.rid.gid == 0) {
            if (o.inCls) {
                System.out.println("\tli\ts4,"+o.rid.id * 4);
                System.out.println("\tadd\ts3,s7,s4");
            } else {
                System.out.println("\tli\ts4,"+o.rid.id*4);
                System.out.println("\tadd\ts3,sp,s4");
            }
        } else {
            System.out.println("\tlui\ts4,%hi(.GLB"+o.rid.gid+")");
            System.out.println("\taddi\ts3,s4,%lo(.GLB"+o.rid.gid+")");
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
        curCls = null;
    }
    @Override
    public void visit(funDef o) { //???
        if (o.typ != null) curRetTyp = glb.getTyp(o.typ);
            else curRetTyp = new primitiveType("void");
        retDone = false;
        cur = o.scp;
        String curnam = o.scp.abs_addr;
        o.nam = curnam;
        funs.put(o.nam, cur);
        System.out.println("\t.text\n\t.align\t2\n\t.globl\t"+curnam+"\n\t.type\t"+curnam+", @function");
        System.out.println(curnam+":");
        System.out.println("\taddi\tsp,sp,"+(cur.allc.cnt + 3) * (-4));
        System.out.println("\tsw\ts0,"+(cur.allc.cnt+2)*4+"(sp)");
        System.out.println("\tsw\tra,"+(cur.allc.cnt+1)*4+"(sp)");
        System.out.println("\taddi\ts0,sp,"+(cur.allc.cnt+3) * 4);
        for (int i = 0; i <o.params.size(); i++) {
            varDefSigStmt x = o.params.get(i);
            varEntity var = cur.getVar(x.nam, x.pos, true);
            if (i <= 7) {
                if (var.vid.gid == 0) {
                    System.out.println("\tsw\ta"+i+","+var.vid.id * 4+"(sp)");
                } else {
                    System.out.println("\tlui\ta"+i+",%hi(.GLB"+var.vid.gid+")");
                    System.out.println("\tsw\ta"+i+",%lo(.GLB"+var.vid.gid+")(s4)");
                }
            }
        }
        if (o.nam.equals("main")) {
            for (int i = 0; i < gVarDefs.size(); i++) {
                varDefSigStmt x = gVarDefs.get(i);
                initGLB(x, i+1);
            }
        }
        o.block.accept(this);
        boolean havret = false;
        int len = o.block.stmtLis.size();
        for (int i = 0; i < len; i++)
            if (o.block.stmtLis.get(i) instanceof returnStmt)
                havret = true;
        if (o.typ != null && o.typ.typ != "void" && !havret) {
            System.out.println("\tli\ts3,0");
            System.out.println("\tli\ta0,0");
        }
        cur = o.scp;
        //o.params.forEach(x->System.out.println(x.nam));
        System.out.println("."+curnam+"_END:");
        System.out.println("\tlw\ts0,"+(cur.allc.cnt+2)*4+"(sp)");
        System.out.println("\tlw\tra,"+(cur.allc.cnt+1)*4+"(sp)");
        System.out.println("\taddi\tsp,sp,"+(cur.allc.cnt+3) * 4);
        System.out.println("\tret");
        System.out.println("\t.size\t"+curnam+", .-"+curnam);
        if (o.nam.equals("main")) {
            retDone = true;
        }
    }
    @Override
    public void visit(typeNode o) {
        o.scp = cur;
    }
}
