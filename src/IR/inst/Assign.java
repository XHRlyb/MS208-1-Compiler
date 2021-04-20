package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Assign extends Inst {
    public Operand val;
    
    public Assign (Block blk, Operand reg, Operand val) {
        super(blk, (Reg)reg);
        this.val = val;
    }

    @Override
    public String toString() {
        return reg.toString() + "=" + 
            (val.typ == null ? "" : val.typ.toString()) + " " +
            val.toString();
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