package IR.inst;
import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public abstract class Inst {
    public Block blk;
    public Reg reg;
    public Inst(Block blk, Reg reg) {
        this.blk = blk;
        this.reg = reg;
    }
    public abstract String toString();
    public abstract void replace(Operand a, Operand b);
    public ArrayList<Operand> Operands() {
        return new ArrayList<>();
    }
}