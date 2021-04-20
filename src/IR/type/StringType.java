package IR.type;
import IR.operand.*;

public class StringType extends BaseType {
    
    public StringType() {}
    
    @Override
    public int size() { return new Pointer(null).size(); }

    @Override
    public String toString() { return "i8*"; }

    @Override
    public boolean equals(BaseType o) {
        return (o instanceof StringType);
    }

    @Override
    public Operand init() { return new Null(); }
}