package IR.operand;

import IR.type.*;

public class Null extends Operand {
    
    public Null() { super(null); }
    public Null(BaseType typ) { super(typ); }

    @Override
    public String toString() { return "null"; }
    @Override
    public boolean equals(Operand o) { 
        return (o instanceof Null); 
    }
}
