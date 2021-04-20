package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Return extends Inst {
    public Operand val;
    
    public Return(Block blk, Operand val) {
        super(blk, null);
        this.val = val;
    }

    @Override
    public String toString() {
        if (val instanceof IR.operand.Void) return "ret void";
        return "ret " + val.typ.toString() + " " + val.toString();
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