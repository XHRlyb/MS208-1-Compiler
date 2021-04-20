package IR.operand;
import IR.type.*;

public abstract class Operand {
    public BaseType typ;
    public Operand(BaseType typ) { this.typ = typ; }
    public abstract String toString();
    public abstract boolean equals(Operand o);
}
