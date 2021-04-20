package IR.type;
import IR.operand.ConstInt;
import IR.operand.Operand;

public class IntType extends BaseType {
    public int siz;
    public IntType(int siz) { this.siz = siz; }
    @Override
    public int size() { return siz; }
    @Override
    public String toString() { return "i"+siz; }
    @Override
    public boolean equals(BaseType o) { 
        return (o instanceof IntType && siz == o.size()); 
    }
    @Override
    public Operand init() { return new ConstInt(0, 32); }
}