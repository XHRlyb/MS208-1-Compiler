package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;

public class CSE {
    public IR ir;

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
    public void doBlock(Block blk) {
        for (int i = 0; i < blk.insts.size(); i++) {
            Inst ins = blk.insts.get(i);
            for (int j = i + 1; j < blk.insts.size(); j++) {
                Inst is = blk.insts.get(j);
                if ((ins instanceof Binary && is instanceof Binary && same((Binary)ins, (Binary)is)) ||
                        (ins instanceof BitCast && is instanceof BitCast && same((BitCast)ins, (BitCast)is)) ||
                        (ins instanceof Cmp && is instanceof Cmp && same((Cmp)ins, (Cmp)is)) ||
                        (ins instanceof GetPtr && is instanceof GetPtr && same((GetPtr)ins, (GetPtr)is)))
                        blk.insts.set(j, new Assign(blk, is.reg, ins.reg));
            }
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> x.blks.forEach(this::doBlock));
    }
}