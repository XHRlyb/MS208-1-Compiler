package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class SCCP {
    public IR ir;
    public Func curFun = null;
    public boolean cond;
    public HashSet<Block> vis;
    public HashMap<Reg, ArrayList<Inst>> regUses;

    public SCCP(IR ir) { this.ir = ir; }

    public void getRegUse() {
        regUses = new HashMap<>();
        for (Block blk : curFun.blks)
            for (Inst ins : blk.insts)
                for (Operand reg : ins.Operands())
                    if (reg instanceof Reg) {
                        if (!regUses.containsKey(reg))
                            regUses.put((Reg)reg, new ArrayList<>());
                        regUses.get(reg).add(ins);
                    }
    }
    public void replace(Reg reg, Operand val) {
        for (Inst ins : regUses.get(reg)) {
            ins.replace(reg, val);
        }
    }
    public boolean doBinary(Binary o) {
        if (o.src1 instanceof ConstInt && o.src2 instanceof ConstInt) {
            ConstInt src1 = (ConstInt) o.src1, src2 = (ConstInt) o.src2;
            if ((o.op.equals("srem") || o.op.equals("sdiv")) && src2.val == 0)
                return false;
            int val = 0;
            switch (o.op) {
                case "add" : val = src1.val + src2.val; break;
                case "sub" : val = src1.val - src2.val; break;
                case "mul" : val = src1.val * src2.val; break;
                case "srem" : val = src1.val % src2.val; break;
                case "sdiv" : val = src1.val / src2.val; break;
                case "shl" : val = src1.val << src2.val; break;
                case "ashr" : val = src1.val >> src2.val; break;
                case "and" : val = src1.val & src2.val; break;
                case "or" : val = src1.val | src2.val; break;
                case "xor" : val = src1.val ^ src2.val; break;
            }
            replace(o.reg, new ConstInt(val, 32));
            return true;
        }
        return false;
    }
    public boolean doCmp(Cmp o) {
        if (o.src1 instanceof ConstInt && o.src2 instanceof ConstInt) {
            ConstInt src1 = (ConstInt) o.src1, src2 = (ConstInt) o.src2;
            boolean val = false;
            switch (o.op) {
                case "slt" : val = src1.val < src2.val; break;
                case "sgt" : val = src1.val > src2.val; break;
                case "sle" : val = src1.val <= src2.val; break;
                case "sge" : val = src1.val >= src2.val; break;
                case "eq" : val = src1.val == src2.val; break;
                case "ne" : val = src1.val != src2.val; break;
            }
            replace(o.reg, new ConstBool(val));
            return true;
        }
        if (o.src1 instanceof ConstBool && o.src2 instanceof ConstBool) {
            ConstBool src1 = (ConstBool) o.src1, src2 = (ConstBool) o.src2;
            boolean val = false;
            switch (o.op) {
                case "eq" : val = src1.val == src2.val; break;
                case "ne" : val = src1.val != src2.val; break;
            }
            replace(o.reg, new ConstBool(val));
            return true;
        }
        return false;
    }
    public boolean doStringCall(Call o) {
        if (o.params.size() < 2) return false;
        if (!(o.params.get(0) instanceof ConstString)) return false;
        if (!(o.params.get(1) instanceof ConstString)) return false;
        ConstString src1 = (ConstString)o.params.get(0), src2 = (ConstString)o.params.get(1);
        if (o.fun.nam.equals("str_add")) {
            ConstString val = new ConstString(src1.nam + "+" + src2.nam, src1.val + src2.val);
            replace(o.reg, val);
            return true;
        }
        int cmp = src1.val.compareTo(src2.val);
        boolean val = false, flg = false;
        switch (o.fun.nam) {
            case "str_lt": val = cmp < 0; break;
            case "str_gt": val = cmp > 0; break;
            case "str_le": val = cmp <= 0; break;
            case "str_ge": val = cmp >= 0; break;
            case "str_eq": val = cmp == 0; break;
            case "str_ne": val = cmp != 0; break;
            default: flg = true;
        }
        if (flg) return false;
        replace(o.reg, new ConstBool(val));
        return true;
    }
    public boolean doPhi(Phi o) {
        Operand t = o.vals.get(0);
        if (!t.isConst()) return false;
        for (Operand oprnd : o.vals)
            if (!t.equals(oprnd)) return false;
        replace(o.reg, t);
        return true;
    }
    public void doBlock(Block blk) {
        vis.add(blk);
        for (int i = 0; i < blk.insts.size(); i++) {
            Inst ins = blk.insts.get(i);
            if (ins instanceof Assign) {
                if (((Assign)ins).val.isConst()) {
                    replace(ins.reg, ((Assign)ins).val);
                    blk.insts.remove(i); i--;
                    cond = true;
                }
            } else if (ins instanceof Binary) {
                if (doBinary((Binary)ins)) {
                    blk.insts.remove(i); i--;
                    cond = true;
                }
            } else if (ins instanceof Cmp) {
                if (doCmp((Cmp)ins)) {
                    blk.insts.remove(i); i--;
                    cond = true;
                }
            } else if (ins instanceof Branch) {
                if (((Branch)ins).cond instanceof ConstBool) {
                    blk.rmvTerm();
                    if (((ConstBool)((Branch)ins).cond).val)
                        blk.addTerm(new Jump(blk, ((Branch)ins).tDest));
                    else
                        blk.addTerm(new Jump(blk, ((Branch)ins).fDest));
                    cond = true;
                }
            } else if (ins instanceof Call) {
                if (doStringCall((Call)ins)) {
                    blk.insts.remove(i); i--;
                    cond = true;
                }
            } else if (ins instanceof Phi) {
                if (doPhi((Phi)ins)) {
                    blk.insts.remove(i); i--;
                    cond = true;
                }
            }
        }
        blk.nex.forEach(x -> {
            if (!vis.contains(x)) doBlock(x);
        });
    }
    public void doFunc(Func fun) {
        curFun = fun;
        cond = true;
        while (cond) {
            cond = false;
            getRegUse();
            vis = new HashSet<>();
            doBlock(fun.begBlk);
        }
        curFun = null;
    }
    public void work() { ir.funs.forEach((s,x) -> doFunc(x)); }
}
