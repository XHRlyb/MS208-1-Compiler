package Optim;

import IR.*;
import IR.inst.*;
import IR.operand.*;
import IR.operand.Void;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Inline {
    public IR ir;
    public Func curFun = null;
    public HashMap<Func, ArrayList<Func>> edges, reEdgeFs;
    public HashMap<Func, ArrayList<Call>> reEdges;
    public ArrayList<Func> vis = new ArrayList<>();
    public HashSet<Func> sta = new HashSet<>();
    public HashSet<Func> nInlin = new HashSet<>();
    public HashSet<Func> cInlin = new HashSet<>();
    public HashSet<Func> inlined = new HashSet<>();
    public int tot = 0;
    public String pre;
    public HashMap<Block, Block> inlineBlk;
    public HashMap<Operand, Operand> inlineOpr;

    public Inline(IR ir) { this.ir = ir; }

    public Block getR(Block x) { return inlineBlk.get(x); }
    public Operand getR(Operand x) {
        if (x == null) return null;
        if (!inlineOpr.containsKey(x)) {
            if (x instanceof Reg && !((Reg)x).glb)
                inlineOpr.put(x, new Reg(x.typ, pre + ((Reg)x).nam, ((Reg)x).glb, ((Reg)x).consptr));
            else
                inlineOpr.put(x, x);
        }
        return inlineOpr.get(x);
    }
    public void getEdge() {
        edges = new HashMap<>();
        reEdges = new HashMap<>();
        reEdgeFs = new HashMap<>();
        ir.funs.forEach((s, x) -> edges.put(x, new ArrayList<>()));
        ir.funs.forEach((s, x) -> reEdges.put(x, new ArrayList<>()));
        ir.funs.forEach((s, x) -> reEdgeFs.put(x, new ArrayList<>()));
        ir.funs.forEach((s, x) -> {
            for (Block blk : x.blks)
                for (Inst ins : blk.insts)
                    if (ins instanceof Call && ir.funs.containsValue(((Call)ins).fun)) { // ???
                        String tp = ((Call)ins).fun.nam;
                        if (tp == "print" || tp == "println" || tp == "printInt" || tp == "printlnInt") continue;
                        if (tp == "getString" || tp == "getInt" || tp == "toString" || tp == "my_malloc") continue;
                        if (tp == "str_length" || tp == "str_substring" || tp == "str_parseInt" || tp == "str_ord") continue;
                        if (tp == "str_add" || tp == "str_lt" || tp == "str_gt" || tp == "str_le") continue;
                        if (tp == "str_ge" || tp == "str_eq" || tp == "str_ne") continue;
                        edges.get(x).add(((Call)ins).fun);
                        reEdges.get(((Call)ins).fun).add((Call)ins);
                        reEdgeFs.get(((Call)ins).fun).add(x);
                    }
        });
    }
    public void doBlock(Block blk) {
        curFun.blks.add(blk);
        blk.nex.forEach(x -> {
            if (!curFun.blks.contains(x)) doBlock(x);
        });
    }
    public void getBlock(Func fun) {
        curFun = fun;
        curFun.blks = new ArrayList<>();
        doBlock(fun.begBlk);
    }
    public void dfs(Func fun) {
        vis.add(fun); sta.add(fun);
        boolean ring = false;
        for (Func x : sta) {
            if (edges.get(fun).contains(x)) ring = true;
            if (ring) nInlin.add(x);
        }
        for (Func x : edges.get(fun))
            if (!vis.contains(x)) dfs(x);
        sta.remove(sta.size() - 1);
    }
    public void inlineCheck() {
        nInlin.add(ir.funs.get("main"));
        dfs(ir.funs.get("main"));
        ir.funs.forEach((s, x) -> {
            if (!nInlin.contains(x)) {
                int num = x.blks.stream().mapToInt(b -> b.insts.size()).sum();
                if (x.blks.size() <= 50 && num <= 300) cInlin.add(x);
            }
        });
    }
    public void inline(Call call, Func caller) {
        Func callee = call.fun;
        int num = callee.blks.stream().mapToInt(b -> b.insts.size()).sum();
        if (callee.blks.size() > 50 || num > 300) return;
        inlineBlk = new HashMap<>();
        inlineOpr = new HashMap<>();
        pre = "inline." + callee.nam + "." + (++tot) + ".";
        callee.blks.forEach(b -> {
            Block nb = new Block(b.lpDep);
            nb.nam = pre + b.nam;
            nb.termed = b.termed;
            inlineBlk.put(b, nb);
        });
        Block begBlk = getR(callee.begBlk);
        Block endBlk = new Block(-1);
        for (Block b : callee.blks) {
            Block nb = getR(b);
            for (Block x : b.pre) nb.pre.add(getR(x));
            for (Block x : b.nex) nb.nex.add(getR(x));
            for (Inst ins : b.insts) {
                if (ins instanceof Assign) {
                    nb.insts.add(new Assign(nb, getR(ins.reg), getR(((Assign)ins).val)));
                } else if (ins instanceof Binary) {
                    nb.insts.add(new Binary(nb, (Reg)getR(ins.reg), ((Binary)ins).op, getR(((Binary)ins).src1), getR(((Binary)ins).src2)));
                } else if (ins instanceof BitCast) {
                    nb.insts.add(new BitCast(nb, (Reg)getR(ins.reg), getR(((BitCast)ins).val)));
                } else if (ins instanceof Branch) {
                    nb.insts.add(new Branch(nb, getR(((Branch)ins).cond), getR(((Branch)ins).tDest), getR(((Branch)ins).fDest)));
                } else if (ins instanceof Call) {
                    Call ni = new Call(nb, (Reg) getR(ins.reg), ((Call)ins).fun);
                    ((Call)ins).params.forEach(x -> ni.params.add(getR(x)));
                    nb.insts.add(ni);
                } else if (ins instanceof Cmp) {
                    nb.insts.add(new Cmp(nb, (Reg) getR(ins.reg), ((Cmp)ins).op, getR(((Cmp)ins).src1), getR(((Cmp)ins).src2)));
                } else if (ins instanceof GetPtr) {
                    nb.insts.add(new GetPtr(nb, getR(ins.reg), getR(((GetPtr)ins).bas), getR(((GetPtr)ins).idx), ((GetPtr)ins).offs));
                } else if (ins instanceof Jump) {
                    nb.insts.add(new Jump(nb, getR(((Jump)ins).dest)));
                } else if (ins instanceof Load) {
                    nb.insts.add(new Load(nb, (Reg) getR(ins.reg), getR(((Load)ins).addr)));
                } else if (ins instanceof Phi) {
                    Phi ni = new Phi(nb, (Reg) getR(ins.reg));
                    ni.domPhi = ((Phi)ins).domPhi;
                    for (int i = 0; i < ((Phi)ins).blks.size(); i++)
                        ni.add(getR(((Phi)ins).blks.get(i)), getR(((Phi)ins).vals.get(i)));
                    nb.insts.add(ni);
                } else if (ins instanceof Return) {
                    nb.insts.add(new Return(nb, getR(((Return)ins).val)));
                    endBlk = nb;
                } else if (ins instanceof Store) {
                    nb.insts.add(new Store(nb, getR(((Store)ins).addr), getR(((Store)ins).val)));
                }
            }
        }
        Block callerBlk = call.blk;
        int pos = 0;
        for (int i = 0; i < callerBlk.insts.size(); i++)
            if (call == callerBlk.insts.get(i)) {
                pos = i;
                break;
            }

        Block cblk1 = new Block(callerBlk.lpDep);
        cblk1.nam = callerBlk.nam + ".inline1";
        cblk1.insts = new ArrayList<>(callerBlk.insts.subList(0, pos));
        for (int i = 0; i < call.params.size(); i++)
            cblk1.addIns(new Assign(cblk1, getR(callee.params.get(i)), call.params.get(i)));
        cblk1.insts.addAll(begBlk.insts);
        cblk1.pre = callerBlk.pre;
        cblk1.nex = begBlk.nex;
        cblk1.termed = true;
        for (Inst ins : cblk1.insts) ins.blk = cblk1;
        cblk1.pre.forEach(b -> {
            for (int i = 0; i < b.nex.size(); i++)
                if (b.nex.get(i) == callerBlk) b.nex.set(i, cblk1);
            b.replaceNex(callerBlk, cblk1);
        });
        cblk1.nex.forEach(b -> {
            for (int i = 0; i < b.pre.size(); i++)
                if (b.pre.get(i) == begBlk) b.pre.set(i, cblk1);
            b.replacePre(begBlk, cblk1);
        });
        if (caller.begBlk == callerBlk) caller.begBlk = cblk1;
        if (endBlk == begBlk) endBlk = cblk1;

        Block cblk2 = new Block(callerBlk.lpDep);
        cblk2.nam = callerBlk.nam + ".inline2";
        Return ret = (Return)endBlk.getTerm();
        endBlk.rmvTerm();
        cblk2.insts = new ArrayList<>(endBlk.insts);
        if (!(ret.val instanceof Void))
            cblk2.insts.add(new Assign(cblk2, call.reg, ret.val));
        cblk2.insts.addAll(callerBlk.insts.subList(pos + 1, callerBlk.insts.size()));
        cblk2.pre = endBlk.pre;
        cblk2.nex = callerBlk.nex;
        cblk2.termed = true;
        for (Inst ins : cblk2.insts) ins.blk = cblk2;
        Block fBlk = endBlk;
        cblk2.pre.forEach(b -> {
            for (int i = 0; i < b.nex.size(); i++)
                if (b.nex.get(i) == fBlk) b.nex.set(i, cblk2);
            b.replaceNex(fBlk, cblk2);
        });
        cblk2.nex.forEach(b -> {
            for (int i = 0; i < b.pre.size(); i++)
                if (b.pre.get(i) == callerBlk) b.pre.set(i, cblk2);
            b.replacePre(callerBlk, cblk2);
        });
        if (caller.begBlk == endBlk) caller.begBlk = cblk2;
        getBlock(caller);
    }
    public void inlineFunc(Func x) {
        if (inlined.contains(x)) return;
        inlined.add(x);
        edges.get(x).forEach(this::inlineFunc);
        for (int i = 0; i < reEdges.get(x).size(); i++)
            inline(reEdges.get(x).get(i), reEdgeFs.get(x).get(i));
    }
    public void work() {
        getEdge();
        inlineCheck();
        for (Func fun : cInlin) inlineFunc(fun);
        for (Map.Entry<String, Func> entry : ir.funs.entrySet()) {
            Func x = entry.getValue();
            if (edges.get(x).contains(x))
                for (int c = 0; c < 10; c++)
                    for (int i = 0; i < reEdges.get(x).size(); i++)
                        inline(reEdges.get(x).get(i), reEdgeFs.get(x).get(i));
        }
    }
}//*/