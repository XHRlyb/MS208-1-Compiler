package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Branch extends Inst {
    public Operand cond;
    public Block tDest, fDest;
    
    public Branch(Block blk, Operand cond, Block tDest, Block fDest) {
        super(blk, null);  //mine!
        this.cond = cond;
        this.tDest = tDest;
        this.fDest = fDest;
    }

    @Override
    public String toString() {
        return "br " + cond.typ.toString() +
        " " + cond.toString() + 
        ", label " + tDest.toString() + 
        ", label " + fDest.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (cond == a) cond = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(cond);
        return ret;
    }
}