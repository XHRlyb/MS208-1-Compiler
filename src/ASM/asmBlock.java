package ASM;

import ASM.inst.*;
import java.util.ArrayList;

public class asmBlock {
    public String nam = null;
    public ArrayList<asmInst> insts = new ArrayList<>();
    public ArrayList<asmBlock> pre = new ArrayList<>();
    public ArrayList<asmBlock> nex = new ArrayList<>();
    public int lpDep;

    public asmBlock(int lpDep) { this.lpDep = lpDep; }
    
    public String toString() { return nam; }

    public void addIns(asmInst a) { insts.add(a); }
    public void addInsF(asmInst a) { insts.add(0, a); }
    public void addInsB(asmInst a) { insts.add(insts.size() - 1, a); }
    public void rmvIns(asmInst a) {
        for (int i = 0; i < insts.size(); i++)
            if (insts.get(i) == a) {
                insts.remove(i);
                return;
            }
    }
    public asmInst getTerm() { return insts.get(insts.size() - 1); }
    public void rmvTerm() { insts.remove(insts.size() - 1); }
}