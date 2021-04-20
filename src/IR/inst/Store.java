package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Store extends Inst {
    public Operand addr, val;

    public Store(Block blk, Operand addr, Operand val) {
        super(blk, null);
        this.addr = addr;
        this.val = val;
    }

    @Override
    public String toString() {
        return "store " + val.typ.toString() + " " + 
            val.toString() + ", " + 
            addr.typ.toString() + " " + addr.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (addr == a) addr = b;
        if (val == a) val = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(addr); ret.add(val);
        return ret;
    }
}