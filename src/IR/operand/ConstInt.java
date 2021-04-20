package IR.operand;
import IR.type.IntType;

public class ConstInt extends Operand {
    public int val;
    public ConstInt(int val, int siz) { 
        super(new IntType(siz));
        this.val = val; 
    }
    @Override
    public String toString() { return val+" "; }
    @Override
    public boolean equals(Operand o) {
        return ((o instanceof ConstInt) && 
                    (((ConstInt)o).val == val));
    }
}
