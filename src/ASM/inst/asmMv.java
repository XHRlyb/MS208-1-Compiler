package ASM.inst;

import ASM.operand.*;
import java.util.HashSet;
import java.util.Collections;

public class asmMv extends asmInst {
    public asmReg reg, src;
    
    public asmMv(asmReg reg, asmReg src) {
        this.reg = reg;
        this.src = src;
    }

    @Override
    public String toString() {
        return "mv " + reg.toString() + ", " + src.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {
        if (src == a) src = b;
    }
    @Override
    public void replaceDef(asmReg a, asmReg b) {
        if (reg == a) reg = b;
    }
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>(Collections.singletonList(src));
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>(Collections.singletonList(reg));
    }
}