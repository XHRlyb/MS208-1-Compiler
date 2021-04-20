package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Phi extends Inst {
    public ArrayList<Block> blks = new ArrayList<>();
    public ArrayList<Operand> vals = new ArrayList<>();
    public Reg phiReg;
    public boolean domPhi = false;
    
    public Phi(Block blk, Reg reg) {
        super(blk, reg);
        this.phiReg = reg;
    }

    public void add(Block blk, Operand val) {
        blks.add(blk); vals.add(val);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(reg.toString() + " = phi " + reg.typ.toString() + " ");
        int Siz = blks.size();
        for (int i = 0; i < Siz; i++) {
            Operand x = vals.get(i);
            Block y = blks.get(i);
            ret.append("[").append(x.toString()).append(", ");
            ret.append(y.toString()).append("]");
            if (i != Siz - 1) ret.append(", ");
        }
        return ret.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        int Siz = vals.size();
        for (int i = 0; i < Siz; i++)
            if (vals.get(i) == a) vals.set(i, b);
    }
    @Override
    public ArrayList<Operand> Operands() {
        return vals;
    }
}