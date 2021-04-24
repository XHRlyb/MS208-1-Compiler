package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;

import java.util.*;

public class ImmInst {
    public IR ir;

    public ImmInst(IR ir) { this.ir = ir; }

    public void doBlock(Block blk) {
        for (int i = 0; i < blk.insts.size() - 1; i++) {
            Inst ins = blk.insts.get(i), is = blk.insts.get(i + 1);
            if (!(ins instanceof Binary) || !(is instanceof Binary)) continue;
            Binary ins1 = (Binary)ins, ins2 = (Binary)is;
            if ((ins1.op.equals("add") || ins1.op.equals("mul")) && ins1.src1 instanceof ConstInt && !(ins1.src2 instanceof ConstInt)) {
                Operand t = ins1.src1;
                ins1.src1 = ins1.src2;
                ins1.src2 = t;
            }
            if ((ins2.op.equals("add") || ins2.op.equals("mul")) && ins2.src1 instanceof ConstInt && !(ins2.src2 instanceof ConstInt)) {
                Operand t = ins2.src1;
                ins2.src1 = ins2.src2;
                ins2.src2 = t;
            }
            if (ins1.op.equals("sub") && ins1.src2 instanceof ConstInt) {
                ins1.op = "add";
                ins1.src2 = new ConstInt(-((ConstInt)ins1.src2).val, 32);
            }
            if (ins2.op.equals("sub") && ins2.src2 instanceof ConstInt) {
                ins2.op = "add";
                ins2.src2 = new ConstInt(-((ConstInt)ins2.src2).val, 32);
            }
            if (ins1.reg == ins2.src1 && ins1.src2 instanceof ConstInt && ins2.src2 instanceof ConstInt && ins1.op.equals(ins2.op)) {
                if (ins1.op.equals("add")) {
                    ins2.src1 = ins1.src1;
                    ins2.src2 = new ConstInt(((ConstInt)ins1.src2).val + ((ConstInt)ins2.src2).val, 32);
                } else if (ins1.op.equals("mul")) {
                    ins2.src1 = ins1.src1;
                    ins2.src2 = new ConstInt(((ConstInt)ins1.src2).val * ((ConstInt)ins2.src2).val, 32);
                }
            }
        }
        for (int i = 0; i < blk.insts.size(); i++) {
            Inst ins = blk.insts.get(i);
            if (ins instanceof Binary) {
                if (((Binary)ins).op.equals("mul") && ((Binary)ins).src2 instanceof ConstInt) {
                    int val = ((ConstInt)((Binary)ins).src2).val;
                    if (val <= 0) continue;
                    int lg2 = (int)(Math.log(val) / Math.log(2));
                    int dt = val - (1 << lg2);
                    if (dt == 0)
                        blk.insts.set(i, new Binary(blk, ins.reg, "shl", ((Binary)ins).src1, new ConstInt(lg2, 32)));
                }
            }
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> x.blks.forEach(this::doBlock));
    }
}