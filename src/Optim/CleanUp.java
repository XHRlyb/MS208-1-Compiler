package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public class CleanUp {
    public IR ir;
    public Func curFun = null;
    public HashSet<Reg> Regs;
    public HashSet<Func> Funs = new HashSet<>();

    public CleanUp(IR ir) { this.ir = ir; }

    public void doBlock(Block blk) {
        blk.nam = "blk." + curFun.blks.size();
        curFun.blks.add(blk);
        blk.nex.forEach(x -> {
            if (!curFun.blks.contains(x)) doBlock(x);
        });
    }
    public void getBlock() {
        curFun.blks = new ArrayList<>();
        doBlock(curFun.begBlk);
    }
    public void getReg() {
        Regs = new HashSet<>();
        for (Block blk : curFun.blks)
            for (Inst ins : blk.insts)
                for (Operand reg : ins.Operands())
                    if (reg instanceof Reg) Regs.add((Reg)reg);
    }
    public void rmvDBlk() {
        curFun.blks.forEach(x -> {
            for (int i = 0; i < x.pre.size(); i++)
                if (!curFun.blks.contains(x.pre.get(i))) {
                    x.pre.remove(i); i--;
                }
        });
    }
    public void rmvDIns() {
        boolean cond = true;
        while (cond) {
            cond = false;
            getReg();
            for (Block blk : curFun.blks)
                for (int i = 0; i < blk.insts.size(); i++) {
                    Inst ins = blk.insts.get(i);
                    if (ins instanceof Call) continue;
                    if (ins.reg != null && !Regs.contains(ins.reg)) {
                        blk.insts.remove(i); i--;
                        cond = true;
                    }
                }
        }
    }
    public void doInst() {
        for (Block blk : curFun.blks)
            for (int i = 0; i < blk.insts.size(); i++) {
                Inst ins = blk.insts.get(i);
                if (ins instanceof Phi) {
                    for (int j = 0; j < ((Phi)ins).blks.size(); j++)
                        if (!blk.pre.contains(((Phi)ins).blks.get(j))) {
                            ((Phi)ins).blks.remove(j);
                            ((Phi)ins).vals.remove(j);
                            j--;
                        }
                    if (((Phi)ins).blks.size() == 1)
                        blk.insts.set(i, new Assign(blk, ins.reg, ((Phi)ins).vals.get(0)));
                }
                ins.Operands().forEach(a -> {
                    if (a instanceof ConstString && !ir.cStrs.containsValue(a)) {
                        ((ConstString)a).nam = "cstr_" + ir.cStrs.size();
                        ir.cStrs.put(((ConstString)a).nam, (ConstString)a);
                    }
                });
                if (ins instanceof Call) Funs.add(((Call)ins).fun);
                ins.blk = blk;
            }

    }
    public void BlockMerge() {
        for (Block blk : curFun.blks)
            if (blk.pre.size() == 1 && blk.pre.get(0).getTerm() instanceof Jump) {
                Block nBlk = blk.pre.get(0);
                nBlk.nex = blk.nex;
                nBlk.rmvTerm();
                nBlk.insts.addAll(blk.insts);
                nBlk.termed = true;
                for (Block b : nBlk.nex) {
                    b.replacePre(blk, nBlk);
                    for (int i = 0; i < b.pre.size(); i++)
                        if (b.pre.get(i) == blk)
                            b.pre.set(i, nBlk);
                }
                for (Inst ins : nBlk.insts) ins.blk = nBlk;
            }
        getBlock();
    }
    public void rmvAssign() {
        HashMap<Reg, Operand> asgMap = new HashMap<>();
        curFun.blks.forEach(b -> b.insts.forEach(x -> {
            if (x instanceof Assign) asgMap.put(x.reg, ((Assign)x).val);
        }));
        curFun.blks.forEach(b -> b.insts.forEach(x -> {  //???
            if (x instanceof Phi)
                ((Phi)x).vals.forEach(t -> {
                    if (t instanceof Reg) asgMap.remove(t);
                });
        }));
        curFun.blks.forEach(t -> {     //???
            for (int i = 0; i < t.insts.size(); i++) {
                Inst x = t.insts.get(i);
                if (asgMap.containsKey(x.reg)) {
                    t.rmvIns(x); i--;
                } else {
                    ArrayList<Operand> oprnds = x.Operands();
                    oprnds.forEach(oprnd -> {
                        if (oprnd instanceof Reg) {
                            Operand rep = oprnd;
                            while (rep instanceof Reg && asgMap.get(rep) != null)
                                rep = asgMap.get(rep);
                            if (oprnd != rep) x.replace(oprnd, rep);
                        }
                    });
                }
            }
        });
    }
    public void doFunc(Func fun) {
        curFun = fun;
        getBlock();
        rmvDBlk();
        rmvDIns();
        doInst();
        BlockMerge();
        rmvAssign();
        curFun = null;
    }
    public void work() {
        ir.cStrs = new HashMap<>();
        ir.funs.forEach((s, x) -> doFunc(x));
        Funs.add(ir.funs.get("main"));
        ir.funs.entrySet().removeIf(x -> !Funs.contains(x.getValue()));
    }
}
