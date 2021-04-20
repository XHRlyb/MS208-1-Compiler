package IR.operand;
import IR.type.BoolType;

public class ConstBool extends Operand {
    public boolean val;
    public ConstBool(boolean val) { 
        super(new BoolType());
        this.val = val; 
    }
    @Override
    public String toString() { return (val?"1":"0"); }
    @Override
    public boolean equals(Operand o) {
        return ((o instanceof ConstBool) && 
                    (((ConstBool)o).val == val));
    }
}
