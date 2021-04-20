package IR.inst;

import IR.Block;
import IR.type.*;
import IR.operand.*;
import java.util.ArrayList;

public class GetPtr extends Inst {
    public Operand bas, idx;
    public ConstInt offs = null;
    
    public GetPtr(Block blk, Operand reg, Operand bas, Operand idx) {
        super(blk, (Reg)reg);
        this.bas = bas;
        this.idx = idx;
    }
    public GetPtr(Block blk, Operand reg, Operand bas, Operand idx, ConstInt offs) {
        super(blk, (Reg)reg);
        this.bas = bas;
        this.idx = idx;
        this.offs = offs;
    }

    @Override
    public String toString() {
        return reg.toString() + " = getelementptr inbounds " + 
            ((Pointer)bas.typ).typ.toString() + ", " + 
            bas.typ.toString() + " " + bas.toString() + ", " + 
            idx.typ.toString() + " " + idx.toString() + (offs != null ? 
            ", " + offs.typ.toString() + " " + offs.toString() : "");
    }
    @Override
    public void replace(Operand a, Operand b) {
        if (bas == a) bas = b;
        if (idx == a) idx = b;
    }
    @Override
    public ArrayList<Operand> Operands() {
        ArrayList<Operand> ret = new ArrayList<>();
        ret.add(bas); ret.add(idx);
        return ret;
    }
}