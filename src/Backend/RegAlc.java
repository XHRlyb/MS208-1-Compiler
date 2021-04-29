package Backend;

import ASM.*;
import ASM.inst.*;
import ASM.operand.*;
import IR.Block;
import IR.inst.Inst;
import IR.inst.Jump;

import java.util.*;

public class RegAlc {
    public ASM asm;
    public asmFunc curFun = null; 
    
    public RegAlc(ASM asm) { this.asm = asm; }

    public LinkedHashMap<asmBlock, HashSet<asmReg>> buses = new LinkedHashMap<>();
    public LinkedHashMap<asmBlock, HashSet<asmReg>> bdefs = new LinkedHashMap<>();
    public LinkedHashMap<asmBlock, HashSet<asmReg>> blvis = new LinkedHashMap<>();
    public LinkedHashMap<asmBlock, HashSet<asmReg>> blvos = new LinkedHashMap<>();

    public void liveness_analysis() {
        buses = new LinkedHashMap<>(); bdefs = new LinkedHashMap<>();
        blvis = new LinkedHashMap<>(); blvos = new LinkedHashMap<>();
        curFun.blks.forEach(b -> {
            HashSet<asmReg> uses = new HashSet<>();
            HashSet<asmReg> defs = new HashSet<>();
            b.insts.forEach(x -> {
                HashSet<asmReg> t = x.Uses();
                t.removeAll(defs);
                uses.addAll(t);
                defs.addAll(x.Defs());
            });
            buses.put(b, uses); blvis.put(b, new HashSet<>());
            bdefs.put(b, defs); blvos.put(b, new HashSet<>());
        });
        HashSet<asmBlock> vis = new HashSet<>();
        Queue<asmBlock> q = new LinkedList<>();
        curFun.blks.forEach(b -> {
            if (b.nex.isEmpty()) { vis.add(b); q.add(b); }
        });
        while (!q.isEmpty()) {
            asmBlock x = q.poll(); vis.remove(x);
            HashSet<asmReg> lvos = new HashSet<>();
            x.nex.forEach(a -> lvos.addAll(blvis.get(a)));
            blvos.replace(x, lvos);
            HashSet<asmReg> lvis = new HashSet<>(lvos);
            lvis.removeAll(bdefs.get(x));
            lvis.addAll(buses.get(x));
            if (!lvis.equals(blvis.get(x))) {
                blvis.replace(x, lvis);
                x.pre.forEach(a -> {
                    if (!vis.contains(a)) { vis.add(a); q.add(a); }
                });
            }
        }
    }


    public static class edge {
        asmReg x, y;

        public edge(asmReg x, asmReg y) {
            this.x = x; this.y = y;
        }

        @Override
        public int hashCode() { return x.hashCode() ^ y.hashCode(); }
        @Override
        public boolean equals(Object o) {
            return (o instanceof edge && ((edge)o).x == x && ((edge)o).y == y);
        }
    }

    public int spOffs = 0;
    public LinkedHashMap<asmReg, HashSet<asmMv>> mvLis = new LinkedHashMap<>();
    public LinkedHashMap<asmReg, HashSet<asmReg>> adjLis = new LinkedHashMap<>();
    public LinkedHashMap<asmReg, Double> wgt = new LinkedHashMap<>();
    public LinkedHashMap<asmReg, Integer> deg = new LinkedHashMap<>();
    public LinkedHashMap<asmReg, asmReg> ali = new LinkedHashMap<>();
    public LinkedHashMap<asmReg, Integer> ofs = new LinkedHashMap<>();
    public HashSet<edge> adjSet = new HashSet<>();
    public int K;
    public HashSet<asmMv> wrMovs, acMovs, caMovs, csMovs, fzMovs;
    public HashSet<asmReg> pCol, ini, spLis, fzLis, slLis; 
    public HashSet<asmReg> spNods, caNods, clNods, cnNods; 
    public Stack<asmReg> sel;

    public void init() {
        K = asm.getCol().size();
        mvLis = new LinkedHashMap<>();
        adjLis = new LinkedHashMap<>();
        wgt = new LinkedHashMap<>();
        deg = new LinkedHashMap<>();
        ali = new LinkedHashMap<>();
        ofs = new LinkedHashMap<>();
        adjSet = new HashSet<>();
        wrMovs = new HashSet<>();
        acMovs = new HashSet<>();
        caMovs = new HashSet<>();
        csMovs = new HashSet<>();
        fzMovs = new HashSet<>();
        pCol = new HashSet<>(asm.getPregs());
        ini = new HashSet<>();
        spLis = new HashSet<>();
        fzLis = new HashSet<>();
        slLis = new HashSet<>();
        spNods = new HashSet<>();
        caNods = new HashSet<>();
        clNods = new HashSet<>();
        cnNods = new HashSet<>();
        sel = new Stack<>();
        curFun.blks.forEach(blk -> {
            blk.insts.forEach(ins ->{
                ini.addAll(ins.Uses());
                ini.addAll(ins.Defs());
            });
        });
        for (asmReg x : ini) {
            mvLis.put(x, new HashSet<>());
            adjLis.put(x, new HashSet<>());
            wgt.put(x, 0.0);
            deg.put(x, 0);
            ali.put(x, x);
            x.col = null;
        }
        ini.removeAll(pCol);
        for (asmReg x : pCol) {
            deg.put(x, 200000000);
            x.col = (Preg)x;
        }
        curFun.blks.forEach(blk -> {
            blk.insts.forEach(ins -> {
                ins.Uses().forEach(x -> {
                    double t  = wgt.get(x) + Math.pow(10.0, blk.lpDep);
                    wgt.replace(x, t);
                });
                ins.Defs().forEach(x -> {
                    double t = wgt.get(x) + Math.pow(10.0, blk.lpDep);
                    wgt.replace(x, t);
                });
            });
        });
    }
    public void add_edge(asmReg x, asmReg y) {
        if (x == y || adjSet.contains(new edge(x, y))) return;
        adjSet.add(new edge(x, y));
        adjSet.add(new edge(y, x));
        if (!pCol.contains(x)) {
            adjLis.get(x).add(y);
            int t = deg.get(x);
            deg.replace(x, t + 1);
        }
        if (!pCol.contains(y)) {
            adjLis.get(y).add(x);
            int t = deg.get(y);
            deg.replace(y, t + 1);
        }
    }
    public HashSet<asmMv> nodeMoves(asmReg x) { //???
        HashSet<asmMv> ret = new HashSet<>(acMovs);
        ret.addAll(wrMovs);
        ret.retainAll(mvLis.get(x));
        return ret;
    }
    public boolean moveRelated(asmReg x) {
        return !nodeMoves(x).isEmpty();
    }
    public HashSet<asmReg> adj(asmReg x) {  //???
        HashSet<asmReg> tmp = new HashSet<>(sel);
        tmp.addAll(caNods);
        HashSet<asmReg> ret = new HashSet<>(adjLis.get(x));
        ret.removeAll(tmp);
        return ret;
    }
    public void enableMoves(HashSet<asmReg> nodes) {
        nodes.forEach(n -> nodeMoves(n).forEach(m -> {
            if (acMovs.contains(m)) {
                acMovs.remove(m);
                wrMovs.add(m);
            }
        }));
    }
    public void decreDegree(asmReg x) { //???
        int d = deg.get(x);
        deg.replace(x, d - 1);
        if (d == K) {
            HashSet<asmReg> t = adj(x);
            t.add(x);
            enableMoves(t);
            slLis.remove(x);
            if (moveRelated(x)) fzLis.add(x);
            else spLis.add(x);
        }
    }
    public void simp() {
        asmReg x = spLis.iterator().next();
        spLis.remove(x);
        sel.push(x);
        adj(x).forEach(this::decreDegree);
    }
    public asmReg getAlias(asmReg x) {
        if (caNods.contains(x))
            return getAlias(ali.get(x));
        return x;
    }
    public void add_workList(asmReg x) {
        if (pCol.contains(x) || moveRelated(x) || deg.get(x) >= K) return;
        fzLis.remove(x);
        spLis.add(x);
    }
    public boolean ok(asmReg x, asmReg y) {
        return deg.get(x) < K || pCol.contains(x) || adjSet.contains(new edge(x, y));
    }
    public boolean ok(ArrayList<asmReg> x, asmReg y) {
        for (asmReg t : x)
            if (!ok(t, y)) return false;
        return true;    
    }
    public boolean consrv(ArrayList<asmReg> nodes, ArrayList<asmReg> y) {
        nodes.removeAll(y);
        nodes.addAll(y);
        int k = 0;
        for (asmReg node : nodes)
            if (deg.get(node) >= K) k++;
        return (k < K);
    }
    public void combine(asmReg x, asmReg y) {
        if (fzLis.contains(y)) fzLis.remove(y);
        else slLis.remove(y);
        caNods.add(y); ali.put(y, x);
        mvLis.get(x).addAll(mvLis.get(y));
        enableMoves(new HashSet<>(Collections.singletonList(y)));
        adj(y).forEach(t -> {
            add_edge(t, x);
            decreDegree(t);
        });
        if (deg.get(x) >= K && fzLis.contains(x)) {
            fzLis.remove(x);
            slLis.add(x);
        }
    }
    public void coales() {
        asmMv m = wrMovs.iterator().next();
        wrMovs.remove(m);
        asmReg x = getAlias(m.reg), y = getAlias(m.src);
        if (pCol.contains(y)) {
            asmReg t = x;
            x = y;
            y = t;
        }
        if (x == y) {
            caMovs.add(m); add_workList(x);
        } else if (pCol.contains(y) || adjSet.contains(new edge(x, y))) {
            caMovs.add(m); add_workList(x); add_workList(y);
        } else if ((pCol.contains(x) && ok(new ArrayList<>(adj(y)), x)) ||
                    (!pCol.contains(x) && consrv(new ArrayList<>(adj(x)), new ArrayList<>(adj(y))))) {
            caMovs.add(m); combine(x, y); add_workList(x);
        } else {
            acMovs.add(m);
        }
    }
    public void freezeMoves(asmReg x) {
        nodeMoves(x).forEach(ins -> {
            asmReg u = ins.reg, v = ins.src, y;
            if (getAlias(x) == getAlias(v)) y = getAlias(u);
            else y = getAlias(v);
            acMovs.remove(ins);
            fzMovs.add(ins);
            if (nodeMoves(y).isEmpty() && deg.get(y) < K) {
                fzLis.remove(y);
                spLis.add(y);
            }
        });
    }
    public void freeze() {
        asmReg x = fzLis.iterator().next();
        fzLis.remove(x);
        spLis.add(x);
        freezeMoves(x);
    }
    public void selectSpill() {
        asmReg m = null;
        double mn = Double.POSITIVE_INFINITY;
        for (asmReg x : slLis) {
            if (cnNods.contains(x) || pCol.contains(x)) continue;
            double t = wgt.get(x) / deg.get(x);
            if (t < mn) { m = x; mn = t; }
        }
        slLis.remove(m);
        spLis.add(m);
        freezeMoves(m);
    }
    public void assignColors() {
        while (!sel.isEmpty()) {
            asmReg n = sel.pop();
            ArrayList<Preg> okCols = new ArrayList<>(asm.getCol());
            HashSet<asmReg> col = new HashSet<>(pCol);
            col.addAll(clNods);
            adjLis.get(n).forEach(w -> {
                if (col.contains(getAlias(w)))
                    okCols.remove(getAlias(w).col);
            });
            if (okCols.isEmpty()) 
                spNods.add(n);
            else {
                clNods.add(n);
                n.col = okCols.get(0);
            }
        }
        for (asmReg x : caNods) x.col = getAlias(x).col;
    }
    public void build() {
        curFun.blks.forEach(b -> {
            HashSet<asmReg> liv = new HashSet<>(blvos.get(b));
            for (int i = b.insts.size() - 1; i >= 0; i--) {
                asmInst ins = b.insts.get(i);
                if (ins instanceof asmMv) {
                    liv.removeAll(ins.Uses());
                    HashSet<asmReg> t = ins.Defs();
                    t.addAll(ins.Uses());
                    t.forEach(x -> mvLis.get(x).add((asmMv)ins));
                    wrMovs.add((asmMv)ins);
                }
                liv.addAll(ins.Defs());
                ins.Defs().forEach(a -> liv.forEach(c -> add_edge(a, c)));
                liv.removeAll(ins.Defs());
                liv.addAll(ins.Uses());
            }
        });
    }
    public void make_workList() {
        ini.forEach(x -> {
            if (deg.get(x) >= K) slLis.add(x);
            else if (moveRelated(x)) fzLis.add(x);
            else spLis.add(x);
        });
    }
    public void rewriteProg() {
        spNods.forEach(v -> {
            ofs.put(v, spOffs);
            spOffs += 4;
        });
        for (int t = 0; t < curFun.blks.size(); t++) {
            asmBlock blk = curFun.blks.get(t);
            for (int i = 0; i < blk.insts.size(); i++) {
                asmInst ins = blk.insts.get(i);
                if (ins instanceof asmMv && spNods.contains(((asmMv)ins).reg) && spNods.contains(((asmMv)ins).src)) {
                    Vreg tmp = new Vreg("tmp");
                    blk.insts.set(i, new asmLoad(tmp, asm.getPreg("sp"),
                            new Imm(ofs.get(((asmMv)ins).src)), 4));
                    blk.insts.add(i + 1, new asmStore(tmp, asm.getPreg("sp"),
                            new Imm(ofs.get(((asmMv)ins).reg)), 4));
                    i++;
                    continue;
                }
                for (asmReg x : ins.Uses())
                    if (spNods.contains(x)) {
                        if (ins instanceof asmMv) {
                            blk.insts.set(i, new asmLoad(((asmMv) ins).reg,
                                    asm.getPreg("sp"), new Imm(ofs.get(x)), 4));
                        } else {
                            Vreg tmp = new Vreg("tmp");
                            cnNods.add(tmp);
                            ins.replaceUse(x, tmp);
                            blk.insts.add(i, new asmLoad(tmp, asm.getPreg("sp"),
                                    new Imm(ofs.get(x)), 4));
                            i++;
                        }
                    }
                for (asmReg x : ins.Defs())
                    if (spNods.contains(x)) {
                        if (ins instanceof asmMv) {
                            blk.insts.set(i, new asmStore(((asmMv)ins).src,
                                asm.getPreg("sp"), new Imm(ofs.get(x)), 4));
                        } else {
                            Vreg tmp = new Vreg("tmp");
                            cnNods.add(tmp);
                            ins.replaceDef(x, tmp);
                            blk.insts.add(i + 1, new asmStore(tmp, asm.getPreg("sp"),
                                                new Imm(ofs.get(x)), 4));
                            i++;
                        }
                    }  
            }
        }
    }
    public void runFunc(asmFunc fun) {
        curFun = fun;
        init();
        liveness_analysis();
        build();
        make_workList();
        while (!spLis.isEmpty() || !wrMovs.isEmpty() || !fzLis.isEmpty() || !slLis.isEmpty()) {
            if (!spLis.isEmpty()) simp();
            else if (!wrMovs.isEmpty()) coales();
            else if (!fzLis.isEmpty()) freeze();
            else selectSpill();
        }
        assignColors();
        if (!spNods.isEmpty()) {
            rewriteProg();
            runFunc(fun);
        } else {
            addSp();
            /*if(fun.nam.equals("taskStress.taskStress")){
                System.err.println("hahahahahahahah");
                new ASMPrinter(System.err,asm).outp();
            }*/

            removeDeadMv();
            BlockMerge();
        }
        curFun = null;
    }
    public void BlockMerge() {
        for (int i = 0; i < curFun.blks.size(); i++) {
            asmBlock blk = curFun.blks.get(i);
            if (blk.pre.size() == 1 && blk.pre.get(0).getTerm() instanceof asmJ && ((asmJ) blk.pre.get(0).getTerm()).dest == blk) {
                asmBlock nBlk = blk.pre.get(0);
                nBlk.nex = blk.nex;
                nBlk.rmvTerm();
                nBlk.insts.addAll(blk.insts);
                for (asmBlock b : nBlk.nex) {
                    for (int j = 0; j < b.pre.size(); j++)
                        if (b.pre.get(j) == blk)
                            b.pre.set(j, nBlk);
                }
                curFun.blks.remove(i);
                i--;
            }
        }
    }
    public void addSp() {
        int rOffs = spOffs + Integer.max(0, curFun.params.size() - 8) * 4;
        if (rOffs > 0) {
            curFun.begBlk.addInsF(new asmCalc(asm.getPreg("sp"), "addi", asm.getPreg("sp"), new Imm(-rOffs)));
            curFun.endBlk.addInsB(new asmCalc(asm.getPreg("sp"), "addi", asm.getPreg("sp"), new Imm(rOffs)));
        }
        for (asmInst ins : curFun.begBlk.insts)
            if (ins instanceof asmLoad && ((asmLoad)ins).offs.inPara)
                ((asmLoad)ins).offs.val += spOffs;
    }
    public void removeDeadMv() {
        for (asmBlock blk : curFun.blks) 
            for (int i = 0; i < blk.insts.size(); i++) {
                asmInst ins = blk.insts.get(i);
                if (ins instanceof asmMv && 
                    ((asmMv)ins).reg.col == ((asmMv)ins).src.col) {
                    blk.insts.remove(i); i--;
                }
            }
    }
    /*public void removeUselessBlock() {
        for (int i = 0; i < curFun.blks.size(); i++) {
            asmBlock blk = curFun.blks.get(i);
            if (!(blk.insts.get(0) instanceof asmJ)) continue;
            asmBlock des = ((asmJ)blk.insts.get(0)).dest;
            for (asmBlock b : curFun.blks) {
                for (int j = 0; j < b.pre.size(); j++)
                    if (b.pre.get(j) == blk) b.pre.set(j, des);
                for (int j = 0; j < b.nex.size(); j++)
                    if (b.nex.get(j) == blk) b.nex.set(j, des);
                for (int j = 0; j < b.insts.size(); j++) {
                    if (b.insts.get(j) instanceof asmJ && ((asmJ)b.insts.get(j)).dest == blk)
                        ((asmJ)b.insts.get(j)).dest = des;
                    if (b.insts.get(j) instanceof asmBranch && ((asmBranch)b.insts.get(j)).dest == blk)
                        ((asmBranch)b.insts.get(j)).dest = des;
                }
            }
            curFun.blks.remove(i);
            i--;
        }
    }*/
    public void work() {
        asm.funs.forEach((s, fun) -> {
            spOffs = 0;
            runFunc(fun);
        });
    }
}