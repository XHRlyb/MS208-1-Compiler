package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class CSE {
    public IR ir;
    public Func curFun = null;
    public LinkedHashMap<Reg, ArrayList<Inst>> regUses;

    public CSE(IR ir) { this.ir = ir; }

    public boolean same(Binary x, Binary y) {
        return x.op.equals(y.op) && x.src1.equals(y.src1) && x.src2.equals(y.src2);
    }
    public boolean same(BitCast x, BitCast y) {
        return x.reg.typ.equals(y.reg.typ) && x.val.equals(y.val);
    }
    public boolean same(Cmp x, Cmp y) {
        return x.op.equals(y.op) && x.src1.equals(y.src1) && x.src2.equals(y.src2);
    }
    public boolean same(GetPtr x, GetPtr y) {
        return x.bas.equals(y.bas) && x.idx.equals(y.idx) && ((x.offs == null && y.offs == null) || (x.offs != null && x.offs.equals(y.offs)));
    }
    public boolean same(ConstInt x, ConstInt y) {
        return x.val == y.val;
    }
    public void getRegUse() {
        regUses = new LinkedHashMap<>();
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
    public void doBlock(Block blk) {
        for (int i = 0; i < blk.insts.size(); i++) {
            Inst ins = blk.insts.get(i);
            for (int j = i + 1; j < blk.insts.size(); j++) {
                Inst is = blk.insts.get(j);
                if ((ins instanceof Binary && is instanceof Binary && same((Binary)ins, (Binary)is)) ||
                        (ins instanceof BitCast && is instanceof BitCast && same((BitCast)ins, (BitCast)is)) ||
                        (ins instanceof Cmp && is instanceof Cmp && same((Cmp)ins, (Cmp)is)) ||
                        (ins instanceof GetPtr && is instanceof GetPtr && same((GetPtr)ins, (GetPtr)is))) {
                    replace(is.reg, ins.reg);
                    blk.insts.remove(j); j--;
                }
            }
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> {
            curFun = x;
            getRegUse();
            x.blks.forEach(this::doBlock);
            curFun = null;
        });
    }
}