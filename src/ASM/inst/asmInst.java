package ASM.inst;

import ASM.operand.*;
import java.util.HashSet;

public abstract class asmInst {
    public abstract String toString();
    public abstract void replaceUse(asmReg a, asmReg b);
    public abstract void replaceDef(asmReg a, asmReg b);
    public abstract HashSet<asmReg> Uses();
    public abstract HashSet<asmReg> Defs();
}