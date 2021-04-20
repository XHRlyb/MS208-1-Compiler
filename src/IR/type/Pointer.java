package IR.type;
import IR.operand.*;

public class Pointer extends BaseType {
    public BaseType typ;
    
    public Pointer(BaseType typ) { this.typ = typ; }
    
    @Override
    public int size() { return 32; }

    @Override
    public String toString() { return typ.toString() + "*"; }

    @Override
    public boolean equals(BaseType o) {
        return (o instanceof Pointer && 
                ((((Pointer)o).typ instanceof VoidType) 
                    || (((Pointer)o).typ.equals(typ))));
    }

    @Override
    public Operand init() { return new Null(); }
}