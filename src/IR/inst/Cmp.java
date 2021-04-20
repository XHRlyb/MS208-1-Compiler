package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Cmp extends Inst {
    public String op;
    public Operand src1, src2;
    
    public Cmp(Block blk, Reg reg, String op, Operand src1, Operand src2) {
        super(blk, reg);
        this.op = op;
        this.src1 = src1;
        this.src2 = src2;
    }

    @Override
    public String toString() {
        return reg.toString() + " = icmp " + op + " " +  
        src1.typ.toString() + " " + src1.toString() + " " + 
        src2.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (src1 == a) src1 = b;
        if (src2 == a) src2 = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(src1); ret.add(src2);
        return ret;
    }
}