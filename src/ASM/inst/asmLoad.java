package ASM.inst;

import ASM.operand.*;
import java.util.HashSet;
import java.util.Collections;

public class asmLoad extends asmInst {
    public asmReg reg, addr;
    public Imm offs;
    public int siz;
    
    public asmLoad(asmReg reg, asmReg addr, Imm offs, int siz) {
        this.reg = reg;
        this.addr = addr;
        this.offs = offs;
        this.siz = siz;
    }

    @Override
    public String toString() {
        return "lw " + reg.toString() + ", " +
            offs.toString() + "(" + addr.toString() + ")";
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {
        if (addr == a) addr = b;
    }
    @Override
    public void replaceDef(asmReg a, asmReg b) {
        if (reg == a) reg = b;
    }
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>(Collections.singletonList(addr));
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>(Collections.singletonList(reg));
    }
}