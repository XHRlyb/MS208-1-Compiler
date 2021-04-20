package ASM.inst;

import ASM.*;
import ASM.operand.*;
import java.util.HashSet;
import java.util.Collections;

public class asmRet extends asmInst {
    public ASM asm;
    
    public asmRet(ASM asm) {
        this.asm = asm;
    }

    @Override
    public String toString() {
        return "ret";
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {}
    @Override
    public void replaceDef(asmReg a, asmReg b) {}
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>(Collections.singletonList(asm.getPreg("ra")));
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>();
    }
}