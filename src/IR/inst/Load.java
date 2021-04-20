package IR.inst;

import IR.Block;
import IR.operand.*;
import java.util.ArrayList;

public class Load extends Inst {
    public Operand addr;
    
    public Load(Block blk, Reg reg, Operand addr) {
        super(blk, reg);
        this.addr = addr;
    }

    @Override
    public String toString() {
        return reg.toString() + " = load " + reg.typ.toString() + ", " +
            addr.typ.toString() + " " + addr.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (addr == a) addr = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(addr);
        return ret;
    }
}