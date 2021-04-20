package ASM.inst;

import ASM.*;
import ASM.operand.*;
import java.util.HashSet;

public class asmCall extends asmInst {
    public asmFunc fun;
    public ASM asm;
    
    public asmCall(asmFunc fun, ASM asm) {
        this.asm = asm;
        this.fun = fun;
    }

    @Override
    public String toString() {
        return "call " + fun.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {}
    @Override
    public void replaceDef(asmReg a, asmReg b) {}
    @Override
    public HashSet<asmReg> Uses() {  //???
        HashSet<asmReg> ret = new HashSet<>();
        for (int i = 0; i < Integer.min(fun.params.size(), 8); i++)
            ret.add(asm.getPreg(10 + i));
        return ret;
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>(asm.getCallerSaves());
    }
}