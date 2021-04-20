package IR.type;
import IR.operand.*;

public class VoidType extends BaseType {
    
    public VoidType() {}
    
    @Override
    public int size() { return 0; } /// mine!

    @Override
    public String toString() { return "void"; }

    @Override
    public boolean equals(BaseType o) {
        return (o instanceof VoidType);
    }

    @Override
    public Operand init() { return new IR.operand.Void(); }
}