package ASM.inst;

import ASM.operand.*;
import java.util.HashSet;
import java.util.Collections;

public class asmCalc extends asmInst {
    public String op;
    public asmOperand rs1, rs2;
    public asmReg rd;

    public asmCalc (asmReg rd, String op, asmOperand rs1, asmOperand rs2) {
        this.rd = rd;
        this.op = op;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }
    @Override
    public String toString() {
        return op + " " + rd.toString() + ", " +
            rs1.toString() + ", " + rs2.toString();
    }
    @Override
    public void replaceUse(asmReg a, asmReg b) {
        if (rs1 == a) rs1 = b;
        if (rs2 == a) rs2 = b;
    }
    @Override
    public void replaceDef(asmReg a, asmReg b) {
        if (rd == a) rd = b;
    }
    @Override
    public HashSet<asmReg> Uses() {
        HashSet<asmReg> ret = new HashSet<>();
        if (rs1 instanceof asmReg) ret.add((asmReg)rs1);
        if (rs2 instanceof asmReg) ret.add((asmReg)rs2);
        return ret;
    }
    @Override
    public HashSet<asmReg> Defs() {
        return new HashSet<>(Collections.singletonList(rd));
    }
}