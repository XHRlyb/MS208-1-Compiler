package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;

import java.lang.reflect.Array;
import java.util.*;

public class LICM {
    public IR ir;
    public Func curFun = null;
    public HashSet<Block> vis;
    public HashMap<Reg, Inst> regDefs;
    public HashMap<Reg, ArrayList<Inst>> gVarDefs;
    public ArrayList<Block> rBlks;
    public ArrayList<Block> rNods;
    public HashMap<Block, Integer> dfn;
    public HashMap<Block, Block> iDom;
    public HashMap<Block, ArrayList<Block>> dCh;
    public HashMap<Block, HashSet<Block>> dSubT;

    public LICM(IR ir) { this.ir = ir; }

    public void getRegDef() {
        regDefs = new HashMap<>();
        gVarDefs = new HashMap<>();
        ir.gVars.forEach((s, x) -> gVarDefs.put(x, new ArrayList<>()));
        for (Block blk : curFun.blks)
            for (Inst ins : blk.insts) {
                if (ins.reg != null) regDefs.put(ins.reg, ins);
                if (ins instanceof Store && ((Store)ins).addr instanceof Reg && ((Reg)((Store)ins).addr).glb)
                    gVarDefs.get(((Store)ins).addr).add(ins);
            }
    }
    public void dfsBlk(Block blk) {
        vis.add(blk);
        blk.nex.forEach(x -> {
            if (!vis.contains(x)) dfsBlk(x);
        });
        rBlks.add(0, blk);
    }
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
    public void dfsTree(Block x) {
        HashSet<Block> sub = new HashSet<>();
        dCh.get(x).forEach(a -> {
            dfsTree(a);
            sub.add(a);
            sub.addAll(dSubT.get(a));
        });
        rNods.add(x);
        dSubT.put(x, sub);
    }
    public void doFunc() {
        for (Block b : rNods) {
            if (!(b.getTerm() instanceof Jump)) continue;
            Block tou = ((Jump)b.getTerm()).dest;
            ArrayList<Block> wei = new ArrayList<>();
            HashSet<Block> sub = dSubT.get(b);
            for (Block blk : sub)
                if (blk.getTerm() instanceof Jump && ((Jump)blk.getTerm()).dest == tou)
                    wei.add(blk);
            if (wei.isEmpty()) continue;
            HashSet<Block> lpBlk = new HashSet<>();
            lpBlk.add(tou);
            lpBlk.addAll(wei);
            Queue<Block> q = new LinkedList<>(wei);
            while (!q.isEmpty()) {
                Block blk = q.poll();
                blk.pre.forEach(x -> {
                    if (!lpBlk.contains(x)) {
                        lpBlk.add(x);
                        q.add(x);
                    }
                });
            }
            boolean cond = true;
            for (Block blk : lpBlk) {
                if (!sub.contains(blk)) cond = false;
                for (Inst ins : blk.insts)
                    if (ins instanceof Call) {
                        cond = false;
                        break;
                    }
            }
            if (!cond) continue;
            for (Block blk : lpBlk)
                for (int i = 0; i < blk.insts.size(); i++) {
                    Inst ins = blk.insts.get(i);
                    if (ins instanceof Binary) {
                        boolean check = true;
                        ArrayList<Operand> uses = ins.Operands();
                        for (Operand oprnd : uses)
                            if (oprnd instanceof Reg && regDefs.containsKey(oprnd) && lpBlk.contains(regDefs.get(oprnd).blk)) {
                                check = false;
                                break;
                            }
                        if (check) {
                            ins.blk = b;
                            b.addInsB(ins);
                            blk.insts.remove(i);
                            i--;
                        }
                    } else if (ins instanceof Load  && ((Load)ins).addr instanceof Reg && ((Reg)((Load)ins).addr).glb) {
                        boolean check = true;
                        ArrayList<Inst> defs = gVarDefs.get(((Load)ins).addr);
                        for (Inst def : defs)
                            if (lpBlk.contains(def.blk)) {
                                check = false;
                                break;
                            }
                        if (check) {
                            ins.blk = b;
                            b.addInsB(ins);
                            blk.insts.remove(i);
                            i--;
                        }
                    }
                }
        }
    }
    public void work() {
        ir.funs.forEach((s, x) -> {
            curFun = x;
            vis = new HashSet<>();
            rBlks = new ArrayList<>();
            dfsBlk(curFun.begBlk);
            dfn = new HashMap<>();
            iDom = new HashMap<>();
            dCh = new HashMap<>();
            domTree();
            rNods = new ArrayList<>();
            dSubT = new HashMap<>();
            dfsTree(curFun.begBlk);
            getRegDef();
            doFunc();
            curFun = null;
        });
    }
}