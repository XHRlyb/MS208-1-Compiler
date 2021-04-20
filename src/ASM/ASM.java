package ASM;

import IR.operand.*;
import ASM.operand.*;
import java.util.HashMap;
import java.util.ArrayList;

public class ASM {
    public HashMap<String, asmFunc> funs = new HashMap<>();
    public HashMap<String, Reg> gVars = new HashMap<>();
    public HashMap<String, ConstString> cStrs = new HashMap<>();
    public HashMap<String, Preg> pregnams = new HashMap<>();
    public HashMap<Integer, Preg> pregids = new HashMap<>();
    public String[] regnam = new String[]{"zero", "ra", "sp", "gp", 
        "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", 
        "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", 
        "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

    public ASM() {
        for (int i = 0; i < 32; i++) {
            Preg a = new Preg(regnam[i]);
            pregnams.put(regnam[i], a);
            pregids.put(i, a);
        }
    }

    public Preg getPreg(String nam) { return pregnams.get(nam); }
    public Preg getPreg(int id) { return pregids.get(id); }
    public ArrayList<Preg> getCallerSaves() {
        ArrayList<Preg> ret = new ArrayList<>();
        for (int i = 1; i <= 1; i++) ret.add(getPreg(i));
        for (int i = 5; i <= 7; i++) ret.add(getPreg(i));
        for (int i = 10; i <= 17; i++) ret.add(getPreg(i));
        for (int i = 28; i <= 31; i++) ret.add(getPreg(i));
        return ret;
    }public ArrayList<Preg> getCalleeSaves() {
        ArrayList<Preg> ret = new ArrayList<>();
        for (int i = 8; i <= 9; i++) ret.add(getPreg(i));
        for (int i = 18; i <= 27; i++) ret.add(getPreg(i));
        return ret;
    }
    public ArrayList<Preg> getCol() {
        ArrayList<Preg> ret = new ArrayList<>();
        for (int i = 5; i <= 7; i++) ret.add(getPreg(i));
        for (int i = 10; i <= 17; i++) ret.add(getPreg(i));
        for (int i = 28; i <= 31; i++) ret.add(getPreg(i));
        for (int i = 8; i <= 9; i++) ret.add(getPreg(i));
        for (int i = 18; i <= 27; i++) ret.add(getPreg(i));
        for (int i = 1; i <= 1; i++) ret.add(getPreg(i));
        return ret;
    }
    public ArrayList<Preg> getPregs() {
        ArrayList<Preg> ret = new ArrayList<>();
        for (int i = 0; i < 32; i++) ret.add(getPreg(i));
        return ret;
    }
}