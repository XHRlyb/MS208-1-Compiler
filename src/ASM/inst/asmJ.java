package ASM.inst;

import ASM.*;
import ASM.operand.*;
import java.util.HashSet;

public class asmJ extends asmInst {
    public asmBlock dest;
    
    public asmJ(asmBlock dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "j " + dest.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {}
    @Override
    public void replaceDef(asmReg a, asmReg b) {}
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>();
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>();
    }
}