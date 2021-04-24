package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;

public class gVarAcc {
    public IR ir;

    public gVarAcc(IR ir) { this.ir = ir; }

    public void doBlock(Block blk) {
        ir.gVars.forEach((s, x) -> {
            Operand val = null;
            boolean nd = false;
            for (int i = 0; i < blk.insts.size(); i++) {
                Inst ins = blk.insts.get(i);
                if (ins instanceof Load && ((Load)ins).addr.equals(x)) {
                    if (val == null)
                        val = ins.reg;
                    else
                        blk.insts.set(i, new Assign(blk, ins.reg, val));
                }
                if (ins instanceof Store && ((Store)ins).addr.equals(x)) {
                    val = ((Store)ins).val;
                    nd = true;
                    blk.insts.remove(i);
                    i--;
                }
                if (ins instanceof Call || i == blk.insts.size() - 1) {
                    if (nd) {
                        blk.insts.add(i, new Store(blk, x, val));
                        i++;
                        nd = false;
                    }
                    val = null;
                }
            }
        });
    }
    public void work() {
        ir.funs.forEach((s, x) -> x.blks.forEach(this::doBlock));
    }
}