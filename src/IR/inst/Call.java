package IR.inst;

import IR.Func;
import IR.Block;
import IR.type.*;
import IR.operand.*;
import java.util.ArrayList;

public class Call extends Inst {
    public Func fun;
    public ArrayList<Operand> params = new ArrayList<>();
    
    public Call(Block blk, Reg reg, Func fun) {
        super(blk, reg);
        this.fun = fun;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (!fun.rettyp.equals(new VoidType()))
            ret.append(reg.toString()).append(" = ");
        ret.append("call ").append(fun.rettyp.toString()).append(" ");
        ret.append(fun.toString()).append("(");
        int Siz = params.size();
        for (int i = 0; i < Siz; i++) {
            Operand x = params.get(i);
            ret.append(x.typ.toString()).append(" ").append(x.toString());
            if (i != Siz - 1) ret.append(", ");
        }
        ret.append(")");
        return ret.toString();
    }
    @Override
    public void replace(Operand a, Operand b) {
        int Siz = params.size();
        for (int i = 0; i < Siz; i++)
            if (params.get(i) == a) params.set(i, b);
    }
    @Override
    public ArrayList<Operand> Operands() {
        return params;
    }
}