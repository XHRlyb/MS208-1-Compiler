package ASM.inst;

import ASM.operand.*;
import java.util.HashSet;
import java.util.Collections;

public class asmLui extends asmInst {
    public asmReg reg;
    public Imm imm;
    
    public asmLui(asmReg reg, Imm imm) {
        this.reg = reg;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return "lui " + reg.toString() + ", " + imm.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {}
    @Override
    public void replaceDef(asmReg a, asmReg b) {
        if (reg == a) reg = b;
    }
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>();
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>(Collections.singletonList(reg));
    }
}