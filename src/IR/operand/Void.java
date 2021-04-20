package IR.operand;

import IR.type.*;

public class Void extends Operand {
    
    public Void() { super(null); }
    
    @Override
    public String toString() { return "void"; }
    @Override
    public boolean equals(Operand o) { 
        return (o instanceof Void); 
    }
}
