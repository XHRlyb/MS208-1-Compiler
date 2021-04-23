package Backend;

import IR.*;
import IR.inst.*;
import IR.type.*;
import IR.operand.*;
import ASM.*;
import ASM.inst.*;
import ASM.operand.*;
import Util.error.internalError;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class ASMBuilder {
    public IR ir;
    public ASM asm;
    public asmFunc curFun = null;
    public asmBlock curBlk = null;
    public HashMap<Operand, asmReg> regMap = new HashMap<>();
    public HashMap<Block, asmBlock> blkMap = new HashMap<>();
    public HashMap<Func, asmFunc> funMap = new HashMap<>();
    
    public ASMBuilder(IR ir, ASM asm) {
        this.ir = ir;
        this.asm = asm;
    }

    public asmFunc getFun(Func fun) {
        if (funMap.get(fun) == null)
            funMap.put(fun, new asmFunc(fun.nam));
        return funMap.get(fun);
    }
    public asmBlock getBlk(Block blk) {
        if (blkMap.get(blk) == null)
            blkMap.put(blk, new asmBlock(blk.lpDep));
        return blkMap.get(blk);
    }
    public asmReg getReg(Operand oprnd) {
        if (oprnd instanceof Reg) {
            if (((Reg)oprnd).glb) throw new internalError("getReg", null);
            else {
                if (regMap.get(oprnd) == null)
                    regMap.put(oprnd, new Vreg(((Reg)oprnd).nam));
                return regMap.get(oprnd);
            }
        } else {     //???
            int val = 0;
            if (oprnd instanceof ConstInt) 
                val = ((ConstInt)oprnd).val;
            if (oprnd instanceof ConstBool)
                val = (((ConstBool)oprnd).val ? 1 : 0);
            if (oprnd instanceof ConstString) {
                Vreg tmp = new Vreg("tmp"), tmp2 = new Vreg("tmp");
                curBlk.addIns(new asmLui(tmp, new Addr(((ConstString)oprnd).nam, 1)));
                curBlk.addIns(new asmCalc(tmp2, "addi", tmp, new Addr(((ConstString)oprnd).nam, 0)));
                return tmp2;
            }
            Vreg tmp = new Vreg("tmp");
            curBlk.addIns(new asmLi(tmp, new Imm(val)));
            return tmp;
        }
    }
    public void assign(asmReg reg, Operand oprnd) {
        if (oprnd instanceof Reg) {
            curBlk.addIns(new asmMv(reg, getReg(oprnd)));
        } else {
            int val = 0;
            if (oprnd instanceof ConstInt) 
                val = ((ConstInt)oprnd).val;
            if (oprnd instanceof ConstBool)
                val = (((ConstBool)oprnd).val ? 1 : 0);
            if (oprnd instanceof ConstString) {
                Vreg tmp = new Vreg("tmp");
                curBlk.addIns(new asmLui(tmp, new Addr(((ConstString)oprnd).nam, 1)));
                curBlk.addIns(new asmCalc(reg, "addi", tmp, new Addr(((ConstString)oprnd).nam, 0)));
                return;
            }
            curBlk.addIns(new asmLi(reg, new Imm(val)));
        }
    }
    public void do_inst(Inst ins) {
        if (ins instanceof Assign) {
            assign(getReg(ins.reg), ((Assign)ins).val);
        } else if (ins instanceof Binary) {
            asmReg rd = getReg(ins.reg), rs1;
            asmOperand rs2;
            String op;
            switch (((Binary)ins).op) {
                case "sdiv": op = "div"; break;
                case "srem": op = "rem"; break;
                case "shl": op = "sll"; break;
                case "ashr": op = "sra"; break;
                default: op = ((Binary)ins).op;
            };
            if (op.equals("mul") || op.equals("div") || op.equals("rem")) {
                rs1 = getReg(((Binary)ins).src1);
                rs2 = getReg(((Binary)ins).src2);
            } else {
                if (((Binary)ins).src2 instanceof ConstInt) {
                    rs1 = getReg(((Binary) ins).src1);
                    if (((ConstInt) ((Binary) ins).src2).val >= -2047 && ((ConstInt) ((Binary) ins).src2).val <= 2047) {
                        rs2 = new Imm(((ConstInt) ((Binary) ins).src2).val);
                        if (op.equals("sub")) {
                            op = "addi";
                            ((Imm) rs2).val = -((Imm) rs2).val;
                        } else {
                            op = op + "i";
                        }
                    } else {
                        rs2 = new Vreg("tmp");
                        curBlk.addIns(new asmLi((asmReg)rs2, new Imm(((ConstInt)((Binary)ins).src2).val)));
                    }
                } else if (((Binary)ins).src1 instanceof ConstInt) {
                    if (op.equals("sll") || op.equals("sra") || op.equals("sub")) {
                        rs1 = getReg(((Binary)ins).src1);
                        rs2 = getReg(((Binary)ins).src2);
                    } else {
                        rs1 = getReg(((Binary)ins).src2);
                        if (((ConstInt) ((Binary) ins).src1).val >= -2047 && ((ConstInt) ((Binary) ins).src1).val <= 2047) {
                            rs2 = new Imm(((ConstInt)((Binary)ins).src1).val);
                            op = op + "i";
                        } else {
                            rs2 = new Vreg("tmp");
                            curBlk.addIns(new asmLi((asmReg)rs2, new Imm(((ConstInt)((Binary)ins).src1).val)));
                        }
                    }
                } else {
                    rs1 = getReg(((Binary)ins).src1);
                    rs2 = getReg(((Binary)ins).src2);
                }
            }
            curBlk.addIns(new asmCalc(rd, op, rs1, rs2));
        } else if (ins instanceof BitCast) {
            curBlk.addIns(new asmMv(getReg(ins.reg), getReg(((BitCast)ins).val)));
        } else if (ins instanceof Branch) {
            if (ins.blk.insts.size() >= 2 &&   ///???
                ins.blk.getBack() instanceof Cmp && 
                ins.blk.getBack().reg == ((Branch)ins).cond) {
                    Cmp cmp = (Cmp)ins.blk.getBack();
                    String op;
                    switch (cmp.op) {
                        case "slt": op = "blt"; break;
                        case "sgt": op = "bgt"; break;
                        case "sle": op = "ble"; break;
                        case "sge": op = "bge"; break;
                        case "eq": op = "beq"; break;
                        case "ne": op = "bne"; break;
                        default: op = "error";
                    };
                    curBlk.addIns(new asmBranch(op, getReg(cmp.src1), getReg(cmp.src2), 
                                                getBlk(((Branch)ins).tDest)));
                    curBlk.addIns(new asmJ(getBlk(((Branch)ins).fDest)));
                } else {
                    curBlk.addIns(new asmBranch("bne", getReg(((Branch)ins).cond), 
                                asm.getPreg("zero"), getBlk(((Branch)ins).tDest)));
                    curBlk.addIns(new asmJ(getBlk(((Branch)ins).fDest)));
                }
        } else if (ins instanceof Call) {
            for (int i = 0; i < Integer.min(((Call)ins).params.size(), 8); i++)
                curBlk.addIns(new asmMv(asm.getPreg("a" + i), getReg(((Call)ins).params.get(i))));
            if (((Call)ins).params.size() > 8) {
                int offs = 0, bas = -(((Call)ins).params.size() - 8) * 4;
                for (int i = 8; i < ((Call)ins).params.size(); i++) {
                    curBlk.addIns(new asmStore(getReg(((Call)ins).params.get(i)), asm.getPreg("sp"), new Imm(offs + bas), 4));
                    offs += 4;
                }
            }
            curBlk.addIns(new asmCall(getFun(((Call)ins).fun), asm));
            if (ins.reg != null)
                curBlk.addIns(new asmMv(getReg(ins.reg), asm.getPreg("a0")));
        } else if (ins instanceof Cmp) {
            switch (((Cmp)ins).op) {
                case "slt":
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "slt", getReg(((Cmp)ins).src1), getReg(((Cmp)ins).src2)));
                    break;
                case "sgt":
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "slt", getReg(((Cmp)ins).src2), getReg(((Cmp)ins).src1)));
                    break;
                case "sle":
                    Vreg tmp = new Vreg("tmp");
                    curBlk.addIns(new asmCalc(tmp, "slt", getReg(((Cmp)ins).src2), getReg(((Cmp)ins).src1)));
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "xori", tmp, new Imm(1)));
                    break;
                case "sge":
                    tmp = new Vreg("tmp");
                    curBlk.addIns(new asmCalc(tmp, "slt", getReg(((Cmp)ins).src1), getReg(((Cmp)ins).src2)));
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "xori", tmp, new Imm(1)));
                    break;
                case "eq":
                    tmp = new Vreg("tmp");
                    curBlk.addIns(new asmCalc(tmp, "xor", getReg(((Cmp)ins).src1), getReg(((Cmp)ins).src2)));
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "sltiu", tmp, new Imm(1)));
                    break;
                case "ne":
                    tmp = new Vreg("tmp");
                    curBlk.addIns(new asmCalc(tmp, "xor", getReg(((Cmp)ins).src1), getReg(((Cmp)ins).src2)));
                    curBlk.addIns(new asmCalc(getReg(ins.reg), "sltu", asm.getPreg("zero"), tmp));
                    break;
                default:
                    break;
            }
        } else if (ins instanceof GetPtr) {
            GetPtr inst = (GetPtr)ins;
            asmReg bas = getReg(inst.bas);
            BaseType typ = ((Pointer)inst.bas.typ).typ;
            int offs = 0;
            if (inst.offs != null)
                offs = ((ClassType)typ).offs(inst.offs.val) / 8;
            if (inst.idx instanceof ConstInt) {
                int w = typ.size() / 8 * ((ConstInt)inst.idx).val + offs;
                curBlk.addIns(new asmCalc(getReg(inst.reg), "addi", bas, new Imm(w)));
            } else {
                Vreg tmp = new Vreg("tmp");
                int siz = typ.size() / 8, lg2 = (int)(Math.log(siz) / Math.log(2));
                if ((1 << lg2) != siz)
                    curBlk.addIns(new asmCalc(tmp, "mul", getReg(inst.idx), getReg(new ConstInt(siz, 32))));
                else
                    curBlk.addIns(new asmCalc(tmp, "slli", getReg(inst.idx), new Imm(lg2)));
                if (offs == 0)
                    curBlk.addIns(new asmCalc(getReg(inst.reg), "add", bas, tmp));
                else {
                    Vreg tmp2 = new Vreg("tmp");
                    curBlk.addIns(new asmCalc(tmp2, "add", bas, tmp));
                    curBlk.addIns(new asmCalc(getReg(inst.reg), "addi", tmp2, new Imm(offs)));
                }
            }
        } else if (ins instanceof Jump) {
            curBlk.addIns(new asmJ(getBlk(((Jump)ins).dest)));
        } else if (ins instanceof Load) {
            if (((Load)ins).addr instanceof Reg && ((Reg)((Load)ins).addr).glb) {
                Vreg tmp = new Vreg("tmp");
                curBlk.addIns(new asmLui(tmp, new Addr( ((Reg) ((Load)ins).addr).nam, 1)));
                curBlk.addIns(new asmLoad(getReg(ins.reg), tmp, new Addr(((Reg) ((Load)ins).addr).nam, 0), 4));
            } else {
                curBlk.addIns(new asmLoad(getReg(ins.reg), getReg(((Load)ins).addr), new Imm(0), 4));
            }
        } else if (ins instanceof Return) {
            if (((Return)ins).val != null)
                assign(asm.getPreg("a0"), ((Return)ins).val);
            for (int i = 0; i < asm.getCalleeSaves().size(); i++)
                curBlk.addIns(new asmMv(asm.getCalleeSaves().get(i), curFun.callSaveVregs.get(i)));
            curBlk.addIns(new asmMv(asm.getPreg("ra"), curFun.raSaveVreg));
            curBlk.addIns(new asmRet(asm));
            curFun.endBlk = curBlk;
        } else if (ins instanceof Store) {
            if (((Store)ins).addr instanceof Reg && ((Reg)((Store)ins).addr).glb) {
                Vreg tmp = new Vreg("tmp");
                curBlk.addIns(new asmLui(tmp, new Addr(((Reg)((Store)ins).addr).nam, 1)));
                curBlk.addIns(new asmStore(getReg(((Store)ins).val), tmp, new Addr(((Reg)((Store)ins).addr).nam, 0), 4));
            } else {
                curBlk.addIns(new asmStore(getReg(((Store)ins).val), getReg(((Store)ins).addr), new Imm(0), 4));
            }
        } else {
            throw new internalError("inst selector error", null);
        }
    }
    public void do_block(Block blk) {
        curBlk = getBlk(blk);
        curBlk.nam = "." + curFun.nam + "." + blk.nam;
        blk.pre.forEach(x -> curBlk.pre.add(getBlk(x)));
        blk.nex.forEach(x -> curBlk.nex.add(getBlk(x)));
        curFun.blks.add(curBlk);
        blk.insts.forEach(this::do_inst);
        curBlk = null;
    }
    public void do_func(Func fun) {
        regMap = new HashMap<>();
        blkMap = new HashMap<>();
        curFun = getFun(fun);
        curFun.begBlk = getBlk(fun.begBlk);
        curBlk = curFun.begBlk;
        asm.funs.put(curFun.nam, curFun);
        fun.params.forEach(x -> curFun.params.add(getReg(x)));
        for (int i = 0; i < asm.getCalleeSaves().size(); i++) {
            Vreg tmp = new Vreg("tmp");
            curFun.callSaveVregs.add(tmp);
            curBlk.addIns(new asmMv(tmp, asm.getCalleeSaves().get(i)));
        }
        Vreg tmp = new Vreg("tmp");  //???
        curFun.raSaveVreg = tmp;
        curBlk.addIns(new asmMv(tmp, asm.getPreg("ra")));
        for (int i = 0; i < Integer.min(curFun.params.size(), 8); i++)
            curBlk.addIns(new asmMv(curFun.params.get(i), asm.getPreg("a" + i)));
        if (curFun.params.size() > 8) {
            int offs = 0;
            for (int i = 8; i < curFun.params.size(); i++) {
                curBlk.addIns(new asmLoad(curFun.params.get(i), asm.getPreg("sp"), new Imm(offs, true), 4));
                offs += 4;
            }
        }
        fun.blks.forEach(this::do_block);
        rmvRIns();
        curFun = null;
    }
    public void rmvRIns() {
        AtomicBoolean cond = new AtomicBoolean(true);
        while (cond.get()) {
            cond.set(false);
            HashSet<asmReg> used = new HashSet<>();
            used.add(asm.getPreg("a0"));
            curFun.blks.forEach(t -> t.insts.forEach(x -> used.addAll(x.Uses())));
            curFun.blks.forEach(t -> {
                for (int i = 0; i < t.insts.size(); i++) {
                    asmInst x = t.insts.get(i);
                    if ((x instanceof asmCalc && !used.contains(((asmCalc)x).rd)) ||
                            (x instanceof asmLi && !used.contains(((asmLi)x).reg)) ||
                            (x instanceof asmLui && !used.contains(((asmLui)x).reg)) ||
                            (x instanceof asmLoad && !used.contains(((asmLoad)x).reg))) {
                        t.rmvIns(x);
                        i--;
                        cond.set(true);
                    }
                }
            });
        }
    }
    public void work() {
        asm.gVars = ir.gVars;
        asm.cStrs = ir.cStrs;
        ir.funs.forEach((s, x) -> do_func(x));
    }
}