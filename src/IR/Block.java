package IR;

import IR.inst.*;
import IR.operand.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Block {
    public String nam;
    public ArrayList<Inst> insts = new ArrayList<>();
    public ArrayList<Block> pre = new ArrayList<>();
    public ArrayList<Block> nex = new ArrayList<>();
    public HashMap<Reg, Operand> pCpy = new HashMap<>();
    public Phi brPhi = null;
    public boolean termed = false;
    public int lpDep;

    public Block(int lpDep) { this.lpDep = lpDep; }
    
    public String toString() { return "%" + nam; }

    public void addIns(Inst a) { insts.add(a); }
    public void addInsF(Inst a) { insts.add(0, a); }
    public void addInsB(Inst a) { insts.add(insts.size() - 1, a); }
    public void rmvIns(Inst a) {
        int Siz = insts.size();
        for (int i = 0; i < Siz; i++)
            if (insts.get(i) == a) {
                insts.remove(i);
                return;
            }
    }
    public Inst getBack() {
        if (insts.size() >= 2) 
            return insts.get(insts.size() - 2);
        return null;
    }
    public void addTerm(Inst a) {
        if (termed) return;
        addIns(a);
        if (a instanceof Jump) {
            nex.add(((Jump)a).dest);
            ((Jump)a).dest.pre.add(this);
        } else if (a instanceof Branch) {
            nex.add(((Branch)a).tDest);
            nex.add(((Branch)a).fDest);
            ((Branch)a).tDest.pre.add(this);
            ((Branch)a).fDest.pre.add(this);
        }
        termed = true;
    }
    public void rmvTerm() {  //???
        if (!termed) return;
        Inst tp = insts.get(insts.size() - 1);
        if (tp instanceof Jump) {
            nex.remove(((Jump)tp).dest);
            ((Jump)tp).dest.pre.remove(this);
        } else if (tp instanceof Branch) {
            nex.remove(((Branch)tp).tDest);
            nex.remove(((Branch)tp).fDest);
            ((Branch)tp).tDest.pre.remove(this);
            ((Branch)tp).fDest.pre.remove(this);
        }
        insts.remove(tp);
        termed = false;
    }
    public Inst getTerm() {
        if (!termed) return null;
        return insts.get(insts.size() - 1);
    }
    public void replacePre(Block oBlk, Block nBlk) {
        for (Inst ins : insts)
            if (ins instanceof Phi)
                for (int i = 0; i < ((Phi)ins).blks.size(); i++)
                    if (((Phi)ins).blks.get(i) == oBlk)
                        ((Phi)ins).blks.set(i, nBlk);
    }
    public void replaceNex(Block oBlk, Block nBlk) {
        for (Inst ins : insts)
            if (ins instanceof Jump) {
                if (((Jump)ins).dest == oBlk)
                    ((Jump)ins).dest = nBlk;
            } else if (ins instanceof Branch) {
                if (((Branch)ins).tDest == oBlk)
                    ((Branch)ins).tDest = nBlk;
                if (((Branch)ins).fDest == oBlk)
                    ((Branch)ins).fDest = nBlk;
            }
    }
}