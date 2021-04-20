package ASM.inst;

import ASM.operand.*;
import java.util.Arrays;
import java.util.HashSet;

public class asmStore extends asmInst {
    public asmReg addr, val;
    public Imm offs;
    public int siz;
    
    public asmStore( asmReg val,asmReg addr, Imm offs, int siz) {
        this.val = val;
        this.addr = addr;
        this.offs = offs;
        this.siz = siz;
    }

    @Override
    public String toString() {
        return "sw " + val.toString() + ", " +
            offs.toString() + "(" + addr.toString() + ")";
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {
        if (val == a) val = b;
        if (addr == a) addr = b;
    }
    @Override
    public void replaceDef(asmReg a, asmReg b) {}
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>(Arrays.asList(val, addr));
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>();
    }
}