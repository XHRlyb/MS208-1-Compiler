package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.*;

public class What {
    public IR ir;

    public What(IR ir) { this.ir = ir; }

    public void doFunc(Func fun) {
        Block endBlk = fun.begBlk;
        for (Block blk : fun.blks)
            for (Inst ins : blk.insts)
                if (ins instanceof Return) {
                    endBlk = blk;
                    break;
                }
        if (endBlk.pre.size() < 2) return;
        ArrayList<Call> doLis = new ArrayList<>();
        for (Block blk : endBlk.pre)
            if (blk.getBack() instanceof Call && ((Call)blk.getBack()).fun == fun)
                doLis.add((Call)blk.getBack());
        if (doLis.isEmpty()) return;
        Block begBlk = fun.begBlk, nbBlk = new Block(0);
        fun.begBlk = nbBlk;
        nbBlk.addTerm(new Jump(nbBlk, begBlk));
        HashMap<Reg, ArrayList<Block>> phiBlks = new HashMap<>();
        HashMap<Reg, ArrayList<Operand>> phiVals = new HashMap<>();
        for (Operand x : fun.params) {
            phiBlks.put((Reg)x, new ArrayList<>(Collections.singletonList(nbBlk)));
            phiVals.put((Reg)x, new ArrayList<>(Collections.singletonList(x)));
        }
        for (int i = 0; i < doLis.size(); i++) {
            Call ins = doLis.get(i);
            ins.blk.rmvTerm();
            ins.blk.rmvTerm();
        }
    }
    public void work() { ir.funs.forEach((s, x) -> doFunc(x)); }
}