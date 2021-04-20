package ASM.inst;

import ASM.asmBlock;
import ASM.operand.*;
import java.util.Arrays;
import java.util.HashSet;

public class asmBranch extends asmInst {
    public String op;
    public asmReg rs1, rs2;
    public asmBlock dest;
    
    public asmBranch(String op, asmReg rs1, asmReg rs2, asmBlock dest) {
        this.op = op;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return op + " " + rs1.toString() + ", " +
            rs2.toString() + ", " + dest.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {
        if (rs1 == a) rs1 = b;
        if (rs2 == a) rs2 = b;
    }
    @Override
    public void replaceDef(asmReg a, asmReg b) {}
    @Override
    public HashSet<asmReg> Uses() {
        return new HashSet<>(Arrays.asList(rs1, rs2));
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>();
    }
}