package IR.operand;
import IR.type.*;

public abstract class Operand {
    public BaseType typ;
    public Operand(BaseType typ) { this.typ = typ; }
    public abstract String toString();
    public abstract boolean equals(Operand o);
    public boolean isConst() {
        return (this instanceof ConstInt || this instanceof ConstBool || this instanceof ConstString);
    }
}
