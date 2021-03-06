package IR.inst;

import IR.Block;
import IR.operand.*;

public class Jump extends Inst {
    public Block dest;
    
    public Jump(Block blk, Block dest) {
        super(blk, null);
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "br label " + dest.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {}
}