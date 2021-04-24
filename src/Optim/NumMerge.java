package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.Queue;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;

public class NumMerge {
    public IR ir;
    public HashMap<Reg, Inst> regDefs;
    public Queue<Inst> q;
    public HashSet<Inst> livIns;

    public NumMerge(IR ir) { this.ir = ir; }
    public void getRegDef(Func fun) {
        regDefs = new HashMap<>();
        for (Block blk : fun.blks)
            for (Inst ins : blk.insts)
                if (ins.reg != null) regDefs.put(ins.reg, ins);
    }
    public void doFunc(Func fun) {
        livIns = new HashSet<>();
        q = new LinkedList<>();
        getRegDef(fun);
        fun.blks.forEach(blk -> blk.insts.forEach(ins -> {
            if (ins instanceof Branch || ins instanceof Call || ins instanceof Jump || ins instanceof Return || ins instanceof Store) {
                livIns.add(ins);
                q.add(ins);
            }
        }));
        while (!q.isEmpty()) {
            Inst ins = q.poll();
            ins.Operands().forEach(oprnd -> {
                if (oprnd instanceof Reg && regDefs.containsKey((oprnd))) {
                    Inst defIns = regDefs.get(oprnd);
                    if (!livIns.contains(defIns)) {
                        livIns.add(defIns);
                        q.add(defIns);
                    }
                }
            });
        }
        fun.blks.forEach(blk -> blk.insts.removeIf(ins -> !livIns.contains(ins)));
    }
    public void work() { ir.funs.forEach((s, x) -> doFunc(x)); }
}