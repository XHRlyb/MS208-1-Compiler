package Backend;

import IR.*;
import IR.inst.*;
import IR.type.*;
import IR.operand.Void;
import IR.operand.*;
import AST.*;
import AST.expression.*;
import AST.statement.*;
import AST.declaration.*;
import Util.symbol.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import Util.error.internalError;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IRBuilder implements ASTVisitor {
    public IR ir;
    public Block curBlk = null;
    public Func curFun = null;
    public ClassType curCls = null;
    public int lpDep = 0;

    public IRBuilder(IR ir) { this.ir = ir; }

    public Reg loadPtr(Operand p) {
        Reg tmp = new Reg(((Pointer)p.typ).typ, "tmp.");
        curBlk.addIns(new Load(curBlk, tmp, p));
        return tmp;
    }

    public Operand getReg(Operand a) {
        if (a.typ instanceof Pointer) return loadPtr(a);
            else return a;
    }

    public void checkBranch(exprNode o) {
        if (o.tBlk == null) return;
        if (o.tBlk.brPhi != null)
            o.tBlk.brPhi.add(curBlk, new ConstBool(true));
        if (o.fBlk.brPhi != null)
            o.fBlk.brPhi.add(curBlk, new ConstBool(false));
        curBlk.addTerm(new Branch(curBlk, getReg(o.oprnd), o.tBlk, o.fBlk));
    }

    public void assign(Operand l, Operand r) {
        if (r instanceof Null) {
            if (((Pointer)l.typ).typ instanceof Pointer)
                curBlk.addIns(new Store(curBlk, l, new Null(((Pointer)l.typ).typ)));
            else
                curBlk.addIns(new Assign(curBlk, l, r));
            return;
        }
        if (l instanceof Reg) {
            if (l.typ instanceof Pointer && l.typ.equals(r.typ)) {
                if (((Reg)l).consptr) {
                    r = getReg(r);
                    curBlk.addIns(new Store(curBlk, l, r));
                } else {
                    curBlk.addIns(new Assign(curBlk, l, r));
                }
            } else if (l.typ instanceof Pointer && ((Pointer)l.typ).typ.equals(r.typ)) {
                curBlk.addIns(new Store(curBlk, l, r));     
            } else {
                r = getReg(r);
                curBlk.addIns(new Assign(curBlk, l, r));
            }
        };// else throw new internalError("assign");
    }


    @Override
    public void visit(programNode o) {
        o.body.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(blockStmt o) {
        for (stmtNode x : o.stmtLis) {
            x.accept(this);
            if (curBlk.termed) break;
        }
    }
    @Override
    public void visit(breakStmt o) {
        if (o.loop instanceof whileStmt) {
            curBlk.addTerm(new Jump(curBlk, ((whileStmt)o.loop).destBlk));
        } else if (o.loop instanceof forStmt) {
            curBlk.addTerm(new Jump(curBlk, ((forStmt)o.loop).destBlk));
        }
    }
    @Override
    public void visit(continueStmt o) {
        if (o.loop instanceof whileStmt) {
            curBlk.addTerm(new Jump(curBlk, ((whileStmt)o.loop).condBlk));
        } else if (o.loop instanceof forStmt) {
            curBlk.addTerm(new Jump(curBlk, ((forStmt)o.loop).incBlk));
        }
    }
    @Override
    public void visit(emptyStmt o) {}
    @Override
    public void visit(exprStmt o) {
        o.expr.accept(this);
    }
    @Override
    public void visit(forStmt o) {
        lpDep++;
        Block lpBlk = new Block(lpDep), cdBlk = new Block(lpDep);
        Block dBlk = new Block(lpDep), icBlk = new Block(lpDep);
        o.destBlk = dBlk;
        o.incBlk = icBlk;
        if (o.ini != null) o.ini.accept(this);
        if (o.cond != null) {
            curBlk.addTerm(new Jump(curBlk, cdBlk));
            curBlk = cdBlk;
            o.cond.tBlk = lpBlk;
            o.cond.fBlk = dBlk;
            o.cond.accept(this);
        } else {
            curBlk.addTerm(new Jump(curBlk, lpBlk));
            cdBlk = lpBlk;
        }
        curBlk = lpBlk;
        o.body.accept(this);
        curBlk.addTerm(new Jump(curBlk, icBlk));
        curBlk = icBlk;
        if (o.inc != null) o.inc.accept(this);
        curBlk.addTerm(new Jump(curBlk, cdBlk));
        curBlk = dBlk;
        lpDep--;
    }
    @Override
    public void visit(ifStmt o) {
        Block tBlk = new Block(lpDep), fBlk = new Block(lpDep);
        Block dBlk = new Block(lpDep);
        o.cond.tBlk = tBlk;
        o.cond.fBlk = ((o.fStmt != null)?fBlk:dBlk);
        o.cond.accept(this);
        curBlk = tBlk;
        o.tStmt.accept(this);
        curBlk.addTerm(new Jump(curBlk, dBlk));
        if (o.fStmt != null) {
            curBlk = fBlk;
            o.fStmt.accept(this);
            curBlk.addTerm(new Jump(curBlk, dBlk));
        }
        curBlk = dBlk;
    }
    @Override
    public void visit(returnStmt o) {
        Inst tmp;
        if (o.retVal != null) {
            o.retVal.accept(this);
            if (o.retVal.oprnd instanceof Reg && ((Reg)o.retVal.oprnd).consptr) {
                tmp = new Return(curBlk, getReg(o.retVal.oprnd));
            } else {
                tmp = new Return(curBlk, o.retVal.oprnd);
            }
        } else {
            tmp = new Return(curBlk, new Void());
        }
        curBlk.addTerm(tmp);
        curFun.retInsts.add(tmp);
    }
    @Override
    public void visit(varDefStmt o) {
        o.varLis.forEach(x -> x.accept(this));
    }
    @Override
    public void visit(varDefSigStmt o) {
        BaseType typ = ir.getTyp(o.var.typ);
        if (o.var.isGlb) {
            Reg reg = new Reg(new Pointer(typ), o.nam);
            reg.glb = reg.consptr = true;
            o.var.oprnd = reg;
            ir.gVars.put(o.nam, reg);
            if (o.expr != null) {
                curFun = ir.funs.get("gVarDef");
                curBlk = ir.gVarDef;
                o.expr.accept(this);
                assign(o.var.oprnd, o.expr.oprnd);
                ir.gVarDef = curBlk;
                curBlk = null;
                curFun = null;
            }
        } else {
            o.var.oprnd = new Reg(typ, o.nam);
            if (curCls != null && curFun == null) {
                o.var.incls = true;
                ((Reg)o.var.oprnd).nam = curCls.nam + "." + o.nam;
                curCls.addVar((Reg)o.var.oprnd);
            } else {
                if (o.expr != null) {
                    o.expr.accept(this);
                    assign(o.var.oprnd, o.expr.oprnd);
                }
            }
        }
    }
    @Override
    public void visit(whileStmt o) {
        Block lpBlk = new Block(lpDep), cdBlk = new Block(lpDep);
        Block dBlk = new Block(lpDep);
        o.destBlk = dBlk;
        o.condBlk = cdBlk;
        curBlk.addTerm(new Jump(curBlk, cdBlk));
        curBlk = cdBlk;
        o.cond.tBlk = lpBlk;
        o.cond.fBlk = dBlk;
        o.cond.accept(this);
        curBlk = lpBlk;
        o.body.accept(this);
        curBlk.addTerm(new Jump(curBlk, cdBlk));
        curBlk = dBlk;
    }

    @Override
    public void visit(binaryExpr o) {
        String op = null, sop = null;
        switch (o.op) {
            case "*": op = "mul"; break;
            case "/": op = "sdiv"; break;
            case "%": op = "srem"; break;
            case "-": op = "sub"; break;
            case "<<": op = "shl"; break;
            case ">>": op = "ashr"; break;
            case "&": op = "and"; break;
            case "^": op = "xor"; break;
            case "|": op = "or"; break;
            case "+": op = "add"; sop = "str_add"; break;
            case "<": op = "slt"; sop = "str_lt"; break;
            case ">": op = "sgt"; sop = "str_gt"; break;
            case "<=": op = "sle"; sop = "str_le"; break;
            case ">=": op = "sge"; sop = "str_ge"; break;
            case "==": op = "eq"; sop = "str_eq"; break;
            case "!=": op = "ne"; sop = "str_ne"; break;
            default: break;
        }
        switch (o.op) {
            case "*": case "/": case "%": case "-":
            case "<<": case ">>": case "&": case "^": case "|":
                o.src1.accept(this); Operand src1 = getReg(o.src1.oprnd);
                o.src2.accept(this); Operand src2 = getReg(o.src2.oprnd);
                o.oprnd = new Reg(src1.typ, "tmp.");
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, op, src1, src2));
                break;

            case "+":
                o.src1.accept(this); src1 = getReg(o.src1.oprnd);
                o.src2.accept(this); src2 = getReg(o.src2.oprnd);
                if (o.src1.typ.isString()) {
                    o.oprnd = new Reg(src1.typ, "tmp.");
                    Func fun = new Func(sop);
                    fun.rettyp = new StringType();
                    Call ins = new Call(curBlk, (Reg)o.oprnd, fun);
                    ins.params.add(src1); ins.params.add(src2);
                    curBlk.addIns(ins);
                } else {
                    o.oprnd = new Reg(src1.typ, "tmp.");
                    curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, op, src1, src2));
                }
                break;

            case "<": case ">": case "<=": case ">=":
                o.src1.accept(this); src1 = getReg(o.src1.oprnd);
                o.src2.accept(this); src2 = getReg(o.src2.oprnd);
                if (o.src1.typ.isString()) {
                    o.oprnd = new Reg(src1.typ, "tmp.");
                    Func fun = new Func(sop);
                    fun.rettyp = new BoolType();
                    Call ins = new Call(curBlk, (Reg)o.oprnd, fun);
                    ins.params.add(src1); ins.params.add(src2);
                    curBlk.addIns(ins);
                } else {
                    o.oprnd = new Reg(new BoolType(), "tmp.");
                    curBlk.addIns(new Cmp(curBlk, (Reg)o.oprnd, op, src1, src2));
                }
                checkBranch(o);
                break;

            case "&&":
                if (o.tBlk != null) {
                    Block tmp = new Block(lpDep);
                    o.src1.tBlk = tmp; o.src2.tBlk = o.tBlk;
                    o.src1.fBlk = o.src2.fBlk = o.fBlk;
                    o.src1.accept(this);
                    curBlk = tmp;
                    o.src2.accept(this);
                } else {
                    Block tmp = new Block(lpDep), dBlk = new Block(lpDep);
                    o.oprnd = new Reg(new BoolType(), "tmp.");
                    Phi phi = new Phi(dBlk, (Reg)o.oprnd);
                    dBlk.brPhi = phi;
                    o.src1.tBlk = tmp; o.src1.fBlk = dBlk;
                    o.src1.accept(this);
                    curBlk = tmp;
                    o.src2.accept(this);
                    phi.add(curBlk, o.src2.oprnd);
                    curBlk.addTerm(new Jump(curBlk, dBlk));
                    curBlk = dBlk;
                    curBlk.addIns(phi);
                }
                break;

            case "||":
                if (o.tBlk != null) {
                    Block tmp = new Block(lpDep);
                    o.src1.tBlk = o.src2.tBlk = o.tBlk;
                    o.src1.fBlk = tmp; o.src2.fBlk = o.fBlk;
                    o.src1.accept(this);
                    curBlk = tmp;
                    o.src2.accept(this);
                } else {
                    Block tmp = new Block(lpDep), dBlk = new Block(lpDep);
                    o.oprnd = new Reg(new BoolType(), "tmp.");
                    o.src1.tBlk = dBlk; o.src1.fBlk = tmp;
                    o.src1.accept(this);
                    Block blk1 = curBlk;
                    curBlk = tmp;
                    o.src2.accept(this);
                    Block blk2 = curBlk;
                    curBlk.addTerm(new Jump(curBlk, dBlk));
                    curBlk = dBlk;
                    Phi phi = new Phi(curBlk, (Reg)o.oprnd); //###
                    phi.add(blk1, new ConstBool(true));
                    phi.add(blk2, o.src2.oprnd);
                    curBlk.addIns(phi);
                }
                break;

            case "==": case "!=":
                o.src1.accept(this);
                o.src2.accept(this);
                if (o.src1.typ.isString()) {
                    src1 = getReg(o.src1.oprnd);
                    src2 = getReg(o.src2.oprnd);
                    o.oprnd = new Reg(src1.typ, "tmp.");
                    Func fun = new Func(sop);
                    fun.rettyp = new BoolType();
                    Call ins = new Call(curBlk, (Reg)o.oprnd, fun);
                    ins.params.add(src1); ins.params.add(src2);
                    curBlk.addIns(ins);
                } else {
                    src1 = o.src1.oprnd; src2 = o.src2.oprnd;
                    if (src1 instanceof Reg && ((Reg)src1).consptr) src1 = getReg(src1);
                    if (src2 instanceof Reg && ((Reg)src2).consptr) src2 = getReg(src2);
                    o.oprnd = new Reg(new BoolType(), "tmp.");
                    curBlk.addIns(new Cmp(curBlk, (Reg)o.oprnd, op, src1, src2));
                }
                checkBranch(o);
                break;

            case "=":
                o.src1.accept(this);
                o.src2.accept(this);
                o.oprnd = o.src1.oprnd;
                assign(o.src1.oprnd, o.src2.oprnd);
                break;

            default:
                break;
        }
    }
    @Override
    public void visit(boolLiteral o) {
        o.oprnd = new ConstBool(o.val);
        checkBranch(o);
    }
    @Override
    public void visit(exprList o) {}
    @Override
    public void visit(funCallExpr o) {
        Operand ths = null;
        if (!(o.bas instanceof varExpr)) {
            o.bas.accept(this);
            if (((Reg)o.bas.oprnd).consptr) 
                ths = getReg(o.bas.oprnd);
            else 
                ths = o.bas.oprnd;
        }
        if (o.bas instanceof memberExpr && ((memberExpr)o.bas).bas.typ instanceof arrayType) {
            Reg bptr = new Reg(new Pointer(new IntType(32)), "tmp.");
            Reg sptr = new Reg(new Pointer(new IntType(32)), "tmp.");
            o.oprnd = new Reg(new IntType(32), "tmp.");
            curBlk.addIns(new BitCast(curBlk, bptr, ths));
            curBlk.addIns(new GetPtr(curBlk, sptr, bptr, new ConstInt(-1, 32)));
            curBlk.addIns(new Load(curBlk, (Reg)o.oprnd, sptr));
            return;
        }
        Func fun = ((funEntity)o.bas.typ).fun;
        o.oprnd = new Reg(fun.rettyp, "tmp.");
        Call ins = new Call(curBlk, (Reg)o.oprnd, fun);
        if (ths != null) ins.params.add(ths);
        if (o.bas instanceof varExpr && fun.inCls)
            ins.params.add(curFun.clsPtr);
        o.params.forEach(x -> {
            x.accept(this);
            if (x.oprnd instanceof Reg && ((Reg)x.oprnd).consptr)
                x.oprnd = getReg(x.oprnd);
            ins.params.add(x.oprnd);
        });
        curBlk.addIns(ins);
        checkBranch(o);
    }
    @Override
    public void visit(intLiteral o) {
        o.oprnd = new ConstInt(o.val, 32);
    }
    @Override
    public void visit(memberExpr o) {
        o.bas.accept(this);
        if (o.isFun) {
            o.oprnd = o.bas.oprnd;
        } else {
            Operand tmp = o.bas.oprnd;
            if (((Reg)tmp).consptr) tmp = getReg(tmp);
            ClassType clsTyp = (ClassType)((Pointer)tmp.typ).typ;
            o.oprnd = new Reg(new Pointer(clsTyp.getVreg(o.nam).typ), "tmp.");
            int offs = clsTyp.getVid(o.nam);
            curBlk.addIns(new GetPtr(curBlk, o.oprnd, tmp, 
                        new ConstInt(0, 32), new ConstInt(offs, 32)));
            ((Reg)o.oprnd).consptr = true;
        }
        checkBranch(o);
    }
    public Reg newww(int dim, newExpr o, BaseType retTyp) {
        BaseType itmTyp = ((Pointer)retTyp).typ;
        Reg dSiz = new Reg(new IntType(32), "tmp.");
        Reg aSiz = new Reg(new IntType(32), "tmp.");
        Reg mPtr = new Reg(new Pointer(new IntType(8)), "tmp.");
        Reg sPtr = new Reg(new Pointer(new IntType(32)), "tmp.");
        Reg tPtr = new Reg(new Pointer(new IntType(32)), "tmp.");
        Reg aPtr = new Reg(retTyp, "tmp.");
        Operand siz = o.exprs.get(dim).oprnd; //?
        if (siz instanceof Reg && ((Reg)siz).consptr) siz = getReg(siz);
        curBlk.addIns(new Binary(curBlk, dSiz, "mul", siz, new ConstInt(itmTyp.size() / 8, 32)));
        curBlk.addIns(new Binary(curBlk, aSiz, "add", dSiz, new ConstInt(4, 32)));
        Call ins = new Call(curBlk, mPtr, ir.mlcFun);
        ins.params.add(aSiz);
        curBlk.addIns(ins);
        curBlk.addIns(new BitCast(curBlk, sPtr, mPtr));
        curBlk.addIns(new Store(curBlk, sPtr, siz));
        curBlk.addIns(new GetPtr(curBlk, tPtr, sPtr, new ConstInt(1, 32)));
        curBlk.addIns(new BitCast(curBlk, aPtr, tPtr));
        if (dim < o.exprs.size() - 1) {
            lpDep++;
            Block lpBlk = new Block(lpDep), icBlk = new Block(lpDep);
            Block dBlk = new Block(lpDep);
            Reg i = new Reg(new IntType(32), "i");
            curBlk.addIns(new Assign(curBlk, i, new ConstInt(0, 32)));
            curBlk.addTerm(new Jump(curBlk, lpBlk));
            curBlk = lpBlk;
            Reg iptr = new Reg(retTyp, "tmp.");
            curBlk.addIns(new GetPtr(curBlk, iptr, aPtr, i));
            Reg iitm = newww(dim + 1, o, itmTyp);
            curBlk.addIns(new Store(curBlk, iptr, iitm));
            curBlk.addTerm(new Jump(curBlk, icBlk));
            curBlk = icBlk;
            curBlk.addIns(new Binary(curBlk, i, "add", i, new ConstInt(1, 32)));
            Reg cond = new Reg(new BoolType(), "tmp.");
            curBlk.addIns(new Cmp(curBlk, cond, "slt", i, siz));
            curBlk.addTerm(new Branch(curBlk, cond, lpBlk, dBlk));
            curBlk = dBlk;
            lpDep--;
        }
        return aPtr;
    }
    @Override
    public void visit(newExpr o) {
        if (o.exprs != null)
            o.exprs.forEach(x -> x.accept(this));
        if (o.typ instanceof arrayType) {
            o.oprnd = newww(0, o, ir.getTyp(o.typ));
        } else {
            Reg mptr = new Reg(new Pointer(new IntType(8)), "tmp.");
            ClassType clsTyp = ((classType)o.typ).clsTyp;
            o.oprnd = new Reg(new Pointer(clsTyp), "tmp.");
            Call ins = new Call(curBlk, mptr, ir.mlcFun);
            ins.params.add(new ConstInt(clsTyp.size() / 8, 32));
            curBlk.addIns(ins);
            curBlk.addIns(new BitCast(curBlk, (Reg)o.oprnd, mptr));
            if (clsTyp.constructor != null) {
                Call ist = new Call(curBlk, null, clsTyp.constructor);
                ist.params.add(o.oprnd);
                curBlk.addIns(ist);
            }
        }
    }
    @Override
    public void visit(nullLiteral o) {
        o.oprnd = new Null();
    }
    @Override
    public void visit(prefixExpr o) {
        o.src.accept(this);
        Operand src = getReg(o.src.oprnd);
        o.oprnd = new Reg(src.typ, "tmp.");
        switch (o.op) {
            case "++":
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, "add", src, new ConstInt(1, 32)));
                assign(o.src.oprnd, o.oprnd);
                break;
            case "--":
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, "sub", src, new ConstInt(1, 32)));
                assign(o.src.oprnd, o.oprnd);
                break;
            case "+": o.oprnd = src; break;
            case "-": 
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, "sub", new ConstInt(0, 32), src));
                break;
            case "~":
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, "xor", src, new ConstInt(Integer.MAX_VALUE, 32)));
                break;
            case "!":
                curBlk.addIns(new Binary(curBlk, (Reg)o.oprnd, "xor", src, new ConstBool(true)));
                break;
            default: break;
        }
        checkBranch(o);
    }
    @Override
    public void visit(stringLiteral o) {
        String nam = "const_str_" + ir.cStrs.size();
        String val = o.val.substring(1, o.val.length() - 1);
        o.oprnd = new ConstString(nam, val);
        ir.cStrs.put(nam, (ConstString)o.oprnd);
    }
    @Override
    public void visit(subscriptExpr o) {
        o.bas.accept(this);
        o.offs.accept(this);
        if (((Reg)o.bas.oprnd).consptr) o.bas.oprnd = getReg(o.bas.oprnd);
        Reg ptr = new Reg(o.bas.oprnd.typ, "tmp.");
        curBlk.addIns(new GetPtr(curBlk, ptr, o.bas.oprnd, getReg(o.offs.oprnd)));
        o.oprnd = ptr;
        ((Reg)o.oprnd).consptr = true;
        checkBranch(o);
    }
    @Override
    public void visit(suffixExpr o) {
        o.src.accept(this);
        Operand src = getReg(o.src.oprnd);
        o.oprnd = new Reg(src.typ, "tmp.");
        assign(o.oprnd, src);
        Reg tmp = new Reg(src.typ, "tmp.");
        switch (o.op) {
            case "++":
                curBlk.addIns(new Binary(curBlk, tmp, "add", src, new ConstInt(1, 32)));
                break;
            case "--":
                curBlk.addIns(new Binary(curBlk, tmp, "sub", src, new ConstInt(1, 32)));
                break;
            default: break;
        }
        assign(o.src.oprnd, tmp);
        checkBranch(o);
    }
    @Override
    public void visit(thisExpr o) {
        o.oprnd = curFun.clsPtr;
    }
    @Override
    public void visit(varExpr o) {
        if (o.var.incls) {
            ClassType clsTyp = (ClassType)((Pointer)curFun.clsPtr.typ).typ;
            o.oprnd = new Reg(new Pointer(clsTyp.getVreg(o.nam).typ), "tmp.");
            int offs = clsTyp.getVid(o.nam);
            curBlk.addIns(new GetPtr(curBlk, o.oprnd, curFun.clsPtr, new ConstInt(0, 32), new ConstInt(offs, 32)));
            ((Reg)o.oprnd).consptr = true;
        } else {
            o.oprnd = o.var.oprnd;
        }
        checkBranch(o);
    }
    @Override
    public void visit(classDef o) {
        curCls = o.clsTyp.clsTyp;
        o.varLis.forEach(x -> x.accept(this));
        o.funLis.forEach(x -> x.accept(this));
        if (o.constructor != null) o.constructor.accept(this);
        ir.mxCls.put(curCls.nam, curCls);
        curCls = null;
    }
    @Override
    public void visit(funDef o) {
        curFun = o.fun.fun;
        if (curCls != null) {
            curFun.nam = curCls.nam + "." + o.nam;
            curCls.funs.add(curFun);
            curFun.clsPtr = new Reg(new Pointer(curCls), "this");
            curFun.params.add(curFun.clsPtr);
        }
        ir.funs.put(curFun.nam, curFun);
        curBlk = curFun.begBlk;
        if (curFun.nam.equals("main")) {
            curBlk.addIns(new Call(curBlk, null, ir.funs.get("gVarDef")));
        }
        o.params.forEach(x -> {
            x.var.oprnd = new Reg(ir.getTyp(x.var.typ), x.nam);
            curFun.params.add(x.var.oprnd);
        });
        o.block.accept(this);
        if (!curBlk.termed) {
            Inst tmp;
            if (o.nam.equals("main"))
                tmp = new Return(curBlk, new ConstInt(0, 32));
            else if (curFun.rettyp.equals(new VoidType()))
                tmp = new Return(curBlk, new Void());
            else if (curFun.retInsts.size() == 0)
                throw new internalError("return lost", null);
            else
                tmp = new Return(curBlk, new Void());
            curBlk.addTerm(tmp);
            curFun.retInsts.add(tmp);
        }
        if (curFun.retInsts.size() > 1) {
            curBlk = new Block(lpDep);
            if (!curFun.rettyp.equals(new VoidType())) {
                Reg tmp = new Reg(curFun.rettyp, "tmp.");
                Phi phi = new Phi(curBlk, tmp);
                curFun.retInsts.forEach(x -> 
                    phi.add(x.blk, ((Return)x.blk.getTerm()).val)
                );
                curBlk.addIns(phi);
                curBlk.addTerm(new Return(curBlk, tmp));
            } else {
                curBlk.addTerm(new Return(curBlk, new Void()));
            }
            curFun.retInsts.forEach(x -> {
                x.blk.rmvTerm();
                x.blk.addTerm(new Jump(x.blk, curBlk));
            });
        }
        curFun = null;
    }
    @Override
    public void visit(typeNode o) {}


    public ArrayList<Block> rBlks = new ArrayList<>();

    public void dfsBlk(Block blk) {
        blk.nam = "block." + curFun.blks.size();
        curFun.blks.add(blk);
        blk.nex.forEach(x -> {
            if (!curFun.blks.contains(x)) dfsBlk(x);
        });
        rBlks.add(0, blk);
    }
    public void rmvDBlk() {
        curFun.blks.forEach(x -> {
            for (int i = 0; i < x.pre.size(); i++) 
                if (x.pre.get(i).nam == null) {
                    x.pre.remove(i);
                    i--;
                }
        });
    }
    public void doEachIns() {
        AtomicInteger tot = new AtomicInteger(); //???
        curFun.params.forEach(x -> {
            if (x instanceof Reg) curFun.vars.add((Reg)x);
        });
        curFun.blks.forEach(t -> t.insts.forEach(x -> {
            if (x instanceof Phi) {
                for (int i = 0; i < ((Phi)x).blks.size(); i++) 
                    if (!curFun.blks.contains(((Phi)x).blks.get(i))) {
                        ((Phi)x).blks.remove(i);
                        ((Phi)x).vals.remove(i);
                        i--;
                    }
            }
            if (x.reg != null) {
                if (!x.reg.nam.equals("tmp.")) {
                    curFun.vars.add(x.reg);
                    x.reg.assign.add(x);
                } else 
                    x.reg.nam = "tmp." + (tot.getAndIncrement());
            }
        }));
    }
    public void renameSameVar() {  //???
        ArrayList<Reg> tp = new ArrayList<>(curFun.vars);
        for (int i = 0; i < tp.size(); i++)
            for (int j = i + 1; j < tp.size(); j++)
                if (tp.get(j).nam.equals(tp.get(i).nam))
                    tp.get(j).nam = tp.get(j).nam + "_rename";
    }

    public HashMap<Block, Integer> dfn = new HashMap<>();
    public HashMap<Block, Block> iDom = new HashMap<>();
    public HashMap<Block, ArrayList<Block>> dCh = new HashMap<>();
    public HashMap<Block, ArrayList<Block>> dFa = new HashMap<>();

    public Block intersect(Block a, Block b) { //???
        if (a == null) return b;
        if (b == null) return a;
        while (a != b) {
            while (dfn.get(a) > dfn.get(b)) a = iDom.get(a);
            while (dfn.get(a) < dfn.get(b)) b = iDom.get(b);
        }
        return a;
    }
    public void domTree() {
        for (int i = 0; i < rBlks.size(); i++) {
            dfn.put(rBlks.get(i), i);
            iDom.put(rBlks.get(i), null);
            dCh.put(rBlks.get(i), new ArrayList<>());
        }
        iDom.replace(curFun.begBlk, curFun.begBlk); //???
        boolean chg = true;
        while (chg) {
            chg = false;
            for (int i = 1; i < rBlks.size(); i++) {
                Block niDom = null;
                for (int j = 0; j < rBlks.get(i).pre.size(); j++) {
                    if (iDom.get(rBlks.get(i).pre.get(j)) != null)
                        niDom = intersect(niDom, rBlks.get(i).pre.get(j));
                }
                if (iDom.get(rBlks.get(i)) != niDom) {
                    iDom.replace(rBlks.get(i), niDom);
                    chg = true;
                }
            }
        }
        iDom.forEach((x, f) -> {
            if (f != null && x != f) dCh.get(f).add(x);
        });
    }
    public void domFrnt() {  //???
        rBlks.forEach(x -> dFa.put(x, new ArrayList<>()));
        rBlks.forEach(x -> {
            if (x.pre.size() >= 2) {
                x.pre.forEach(p -> {
                    Block r = p;
                    while (r != iDom.get(x)) {
                        dFa.get(r).add(x);
                        r = iDom.get(r);
                    }
                });
            }
        });
    }
    public void getPhi() { //???
        dfn = new HashMap<>(); iDom = new HashMap<>();
        dCh = new HashMap<>(); dFa = new HashMap<>();
        domTree(); domFrnt();
        curFun.vars.forEach(x -> {
            HashSet<Block> has = new HashSet<>();
            for (int i = 0; i < x.assign.size(); i++) {
                Inst p = x.assign.get(i);
                dFa.get(p.blk).forEach(b -> {
                    if (!has.contains(b)) {
                        Phi t = new Phi(b, x);
                        t.domPhi = true;
                        b.addInsF(t);
                        x.assign.add(t);
                        has.add(b);
                    }
                });
            }
        });
    }
    public void renameVar(Reg x, Block blk) {  //???
        Reg ve = x.renams.peek();
        blk.insts.forEach(s -> {
            if (!(s instanceof Phi) || !((Phi)s).domPhi)
                s.replace(x, x.renams.peek());
            if (s.reg != null && s.reg == x) {
                s.reg = new Reg(x.typ, x.nam + "_" + (x.rnmid++));
                x.renams.push(s.reg);
            }
        });
        blk.nex.forEach(s -> {
            s.insts.forEach(ins -> {
                if (ins instanceof Phi && ((Phi)ins).phiReg == x) {
                    if (x.renams.size() > 1)
                        ((Phi) ins).add(blk, x.renams.peek());
                    else
                        ((Phi) ins).add(blk, x.typ.init());
                }
            });
        });
        dCh.get(blk).forEach(s -> renameVar(x, s));
        while (x.renams.peek() != ve) x.renams.pop();
    }
    public void simpPhi() {
        curFun.blks.forEach(t -> {
            for (int i = 0; i < t.insts.size(); i++) {
                Inst ins = t.insts.get(i);
                if (ins instanceof Phi && ((Phi)ins).vals.size() == 1)
                    t.insts.set(i, new Assign(t, ins.reg, ((Phi)ins).vals.get(0)));
            }
        });
        curFun.blks.forEach(t -> {
            HashMap<Reg, Phi> phis = new HashMap<>();
            t.insts.forEach(x -> {
                if (x instanceof Phi) phis.put(x.reg, (Phi)x);
            });
            for (int i = 0; i < t.insts.size(); i++) {
                Inst x = t.insts.get(i);
                if (x instanceof Phi) {
                    for (int j = 0; j < ((Phi)x).vals.size(); j++) 
                        if (((Phi)x).vals.get(j) instanceof Reg &&
                            phis.get(((Phi)x).vals.get(j)) != null) {
                                Phi a = phis.get(((Phi)x).vals.get(j));
                                for (int k = 0; k < a.vals.size(); k++) 
                                    if (a.blks.get(k) == ((Phi)x).blks.get(j))
                                        ((Phi)x).vals.set(j, a.vals.get(k));
                            }
                }
            }
        });
        AtomicBoolean cond = new AtomicBoolean(true);
        while (cond.get()) {
            cond.set(false);
            curFun.blks.forEach(t -> t.insts.forEach(x -> {
                ArrayList<Operand> oprnds = x.Operands();
                oprnds.forEach(oprnd -> {
                    if (oprnd instanceof Reg)
                        ((Reg)oprnd).used = true;
                });
            }));
            curFun.blks.forEach(t -> {  //???
                for (int i = 0; i < t.insts.size(); i++) {
                    Inst x = t.insts.get(i);
                    if (x instanceof Phi) {
                        if (!x.reg.used) {
                            t.rmvIns(x);
                            i--;
                            cond.set(true);
                        } else 
                            x.reg.used = false;
                    }
                }
            });
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> {
            curFun = x;
            ir.gVarDef.addTerm(new Return(ir.gVarDef, new Void()));
            dfsBlk(x.begBlk);
            rmvDBlk();
            doEachIns();
            renameSameVar();
            getPhi();
            curFun.vars.forEach(v -> {
                v.renams.push(new Reg(v.typ, v.nam + "_" + (v.rnmid++)));
                for (int i = 0; i < curFun.params.size(); i++)
                    if (curFun.params.get(i) == v) {
                        v.renams.push(new Reg(v.typ, v.nam + "_" + (v.rnmid++)));
                        curFun.params.set(i, v.renams.peek());
                    }
                renameVar(v, curFun.begBlk);
            });
            simpPhi();
        });
    }
}