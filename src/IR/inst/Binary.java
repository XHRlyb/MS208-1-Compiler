package IR.inst;
import IR.Block;
import IR.operand.*;

import java.util.ArrayList;

public class Binary extends Inst {
    public String op;
    public Operand src1, src2;
    public Binary (Block blk, Reg reg, String op, Operand s1, Operand s2) {
        super(blk, reg);
        this.op = op;
        this.src1 = s1;
        this.src2 = s2;
    }
    @Override
    public String toString() {
        return reg.toString() + "=" + 
            op + " " +
            src1.toString() + " " + 
            src1.toString() + "," + src2.toString();
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