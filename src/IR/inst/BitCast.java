package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class BitCast extends Inst {
    public Operand val;

    public BitCast(Block blk, Reg reg, Operand val) { //mine!
        super(blk, reg);
        this.val = val;
    }

    @Override
    public String toString() {
        return reg.toString() + " = bitcast " + 
            val.typ.toString() + " " + val.toString() + 
            " to " + reg.typ.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (val == a) val = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(val);
        return ret;
    }
}